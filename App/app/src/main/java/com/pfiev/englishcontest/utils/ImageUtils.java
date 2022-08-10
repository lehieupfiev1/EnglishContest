package com.pfiev.englishcontest.utils;

import android.app.Activity;
import android.content.Context;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public final class ImageUtils {
    private static final String TAG = ImageUtils.class.getCanonicalName();
    public static String MIMETYPE_PNG = "image/png";
    public static String MIMETYPE_JPEG = "image/jpeg";
    private static int MAX_TEXTURE = -1;

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
}