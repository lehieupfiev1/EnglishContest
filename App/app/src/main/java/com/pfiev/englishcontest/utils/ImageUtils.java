package com.pfiev.englishcontest.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.Glide;
import com.pfiev.englishcontest.model.UserItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ImageUtils {
    private static final String TAG = ImageUtils.class.getCanonicalName();
    public static String MIMETYPE_PNG = "image/png";
    public static String MIMETYPE_JPEG = "image/jpeg";
    private static int MAX_TEXTURE = -1;
    public static final String CONTENT_SCHEME_PHOTO_TEMP_PATH = "/content_scheme_photo_temp.jpg";
    public static final String AVATAR_TEMP_FILE = "/avatar_temp.png";

    private static final long MAX_CACHE_BYTES = 1024 * 1024 * 12;

    private ImageUtils() {
    }

    public static int getMaxTexture() {
        if (MAX_TEXTURE == -1) {
            EGLDisplay dpy = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            int[] vers = new int[2];
            EGL14.eglInitialize(dpy, vers, 0, vers, 1);

            int[] configAttr = {
                    EGL14.EGL_COLOR_BUFFER_TYPE, EGL14.EGL_RGB_BUFFER, EGL14.EGL_LEVEL, 0, EGL14.EGL_RENDERABLE_TYPE,
                    EGL14.EGL_OPENGL_ES2_BIT, EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT, EGL14.EGL_NONE};
            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfig = new int[1];
            EGL14.eglChooseConfig(dpy, configAttr, 0, configs, 0, 1, numConfig, 0);
            if (numConfig[0] == 0) {
                Log.e(TAG, "No config found."); // TROUBLE! No config found.
            }
            EGLConfig config = configs[0];

            int[] surfAttr = {
                    EGL14.EGL_WIDTH, 64, EGL14.EGL_HEIGHT, 64, EGL14.EGL_NONE};
            EGLSurface surf = EGL14.eglCreatePbufferSurface(dpy, config, surfAttr, 0);

            int[] ctxAttrib = {
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE};
            EGLContext ctx = EGL14.eglCreateContext(dpy, config, EGL14.EGL_NO_CONTEXT, ctxAttrib, 0);

            EGL14.eglMakeCurrent(dpy, surf, surf, ctx);

            int[] maxSize = new int[1];
            GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxSize, 0);

            EGL14.eglMakeCurrent(dpy, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroySurface(dpy, surf);
            EGL14.eglDestroyContext(dpy, ctx);
            EGL14.eglTerminate(dpy);
            MAX_TEXTURE = maxSize[0];
        }
        return MAX_TEXTURE;
    }

    public static boolean isLoadable(Context context, ImageView imageView) {
        if (context instanceof Activity && !Utility.isValid((Activity) context)) {
            return false;
        } else if (context instanceof android.view.ContextThemeWrapper) {
            return true;
        } else if (context instanceof Activity && Utility.isValid((Activity) context)) {
            return true;
        } else {
            if (context != null) {
                Glide.with(context).clear(imageView);
            }
            return false;
        }
    }

    public static boolean saveBitmap(Bitmap bitmap, Bitmap.CompressFormat format, String path) {
        if (bitmap == null || path == null || bitmap.isRecycled()) {
            return false;
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            bitmap.compress(format, 90, out);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap getBitmap(Context context, String path, float degree) {
        Bitmap bitmap = null;
        try {
            if (path != null) {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);

                int width = options.outWidth;
                int height = options.outHeight;

                if (width > 800) {
                    int reqWidth = 800;
                    int reqHeight = (int) ((double) height / width * reqWidth);

                    options.inSampleSize = ImageUtils.calculateLargestInSampleSize(options, reqWidth, reqHeight);
                }

                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(path, options);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Could not find the file", Toast.LENGTH_SHORT).show();
            Log.e(TAG,  "Could not find the file :[" + path + "]");
            e.printStackTrace();
            return null;
        }

        if (bitmap != null) {
            bitmap = rotateImage(bitmap, degree);
        } else {
            Toast.makeText(context, "Selected bitmap is null. Unable to initialize CropImageView", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Selected bitmap is null. Unable to initialize CropImageView.");
            return null;
        }

        return bitmap;
    }

    public static int calculateLargestInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static float getDegreeOfPath(String path) {
        float rotate = 0;

        if (path == null) {
            Log.e(TAG, "[getDegreeOfPath] The path is null...");
            return rotate;
        }

        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                default:
                    rotate = 0;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rotate;
    }

    public static Bitmap rotateImage(Bitmap bitmap, float degree) {
        if (bitmap == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static String getTempPath(String filename, Context mContext) {
        File file = new File(mContext.getApplicationContext().getExternalCacheDir().getPath() + filename);
        if (file.exists()) {
            if (file.isDirectory()) {
                return null;
            }
        } else {
            try {
                if (!file.createNewFile()) {
                    return null;
                }
            } catch (IOException e) {}
        }

        return Uri.fromFile(file).getPath();
    }

    public static void deleteFile(String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            if (!file.delete()) {
                Log.i(TAG, "Failed to delete file/folder");
            }
        }
    }

    public static String getFilepathFromContentUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }

        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            return uri.getPath();
        }

        if (uri.toString().startsWith(ContentResolver.SCHEME_CONTENT)) {
            return getImagePathFromInputStreamUri(context, uri);
        } else {
            try (final Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DATA},
                    null, null, null)) {
                String path = null;
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToNext();
                    path = cursor.getString(0);
                }

                return path;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        return null;
    }

    private static String getImagePathFromInputStreamUri(Context context, Uri uri) {
        InputStream inputStream = null;
        String filePath = null;

        if (uri.getAuthority() != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                File photoFile = createTempFileFrom(inputStream, context);

                if (photoFile != null) {
                    filePath = photoFile.getPath();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                Toast.makeText(context, "File not support", Toast.LENGTH_SHORT).show();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return filePath;
    }

    private static File createTempFileFrom(InputStream inputStream, Context mContext) throws IOException {
        File targetFile = null;

        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[8 * 1024];

            targetFile = new File(mContext.getApplicationContext().getExternalCacheDir().getPath() +CONTENT_SCHEME_PHOTO_TEMP_PATH);
            try (OutputStream outputStream = new FileOutputStream(targetFile)) {
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
            }
        }

        return targetFile;
    }

    public static boolean createIfNotExists(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                // TODO : Remove directory and create new file
                Log.e(TAG,  "Failed to create temp file. " + AVATAR_TEMP_FILE + " is a directory.");
                return false;
            }
        } else {
            try {
                if (!file.createNewFile()) {
                    Log.e(TAG,"Failed to create temp file. " +AVATAR_TEMP_FILE);
                    return false;
                }
            } catch (IOException e) {
                Log.w(TAG,"createNewFile, ", e);
            }
        }

        return true;
    }

    public static String getFileExtension(Activity activity, Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.getContentResolver().getType(uri));
    }
}