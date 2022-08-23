package com.pfiev.englishcontest.ui.wiget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.utils.ImageUtils;

@SuppressLint("AppCompatCustomView")
public class RoundedAvatarImageView extends ImageView {
    private static final String TAG = RoundedAvatarImageView.class.getCanonicalName();
    private final boolean mShadowEnabled;

    private String mUrl;

    public static class RoundedAvatarDrawable extends Drawable {

        private static final float SHADOW_DIMENSION = 3.0f;
        private static final int SHADOW_COLOR = 0x33000000;

        private boolean mHasAvatar;
        private final Paint mPaint;
        private final RectF mRectF;
        private final int mBitmapWidth;
        private final int mBitmapHeight;
        private final Paint mShadowPaint;
        private final boolean mShadowEnabled;

        public RoundedAvatarDrawable(Bitmap bitmap, boolean shadowEnabled) {
            if (bitmap != null) {
                mHasAvatar = true;
                mBitmapWidth = bitmap.getWidth();
                mBitmapHeight = bitmap.getHeight();
            } else {
                mHasAvatar = false;
                mBitmapWidth = 0;
                mBitmapHeight = 0;
            }

            mPaint = new Paint();
            mRectF = new RectF();

            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            final BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaint.setShader(shader);

            mShadowPaint = new Paint();
            mShadowPaint.setColor(SHADOW_COLOR);
            mShadowEnabled = shadowEnabled;
        }

        @Override
        public void draw(Canvas canvas) {
            RectF shadowRect = new RectF(mRectF);

            if (mShadowEnabled) {
                shadowRect.right -= (SHADOW_DIMENSION / 2);
                shadowRect.bottom -= SHADOW_DIMENSION;
                shadowRect.left += (SHADOW_DIMENSION / 2);

                canvas.drawOval(mRectF, mShadowPaint);
            }
            canvas.drawOval(shadowRect, mPaint);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            mRectF.set(bounds);
        }

        @Override
        public void setAlpha(int alpha) {
            if (mPaint.getAlpha() != alpha) {
                mPaint.setAlpha(alpha);
                invalidateSelf();
            }
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            mPaint.setColorFilter(cf);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public int getIntrinsicWidth() {
            return mBitmapWidth;
        }

        @Override
        public int getIntrinsicHeight() {
            return mBitmapHeight;
        }

        public void setAntiAlias(boolean aa) {
            mPaint.setAntiAlias(aa);
            invalidateSelf();
        }

        @Override
        public void setFilterBitmap(boolean filter) {
            mPaint.setFilterBitmap(filter);
            invalidateSelf();
        }

        @Override
        public void setDither(boolean dither) {
            mPaint.setDither(dither);
            invalidateSelf();
        }

        public boolean hasAvatar() {
            return mHasAvatar;
        }
    }

    public RoundedAvatarImageView(Context context) {
        this(context, null);
    }

    public RoundedAvatarImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray sa = getResources().obtainAttributes(attrs, R.styleable.RoundedAvatarImageView);
        mShadowEnabled = sa.getBoolean(R.styleable.RoundedAvatarImageView_shadowEnabled, false);
        sa.recycle();
    }

    public void setDefaultDrawable() {
        setImageResource(R.drawable.bg_profile_image);
        setScaleType(ScaleType.FIT_CENTER);
    }

    public void setAdjustAvatarDrawable(Bitmap bitmap) {
        if (getWidth() > 0 && getHeight() > 0) {
            try {
                setImageDrawable(new RoundedAvatarDrawable(Bitmap.createScaledBitmap(bitmap, getWidth(), getHeight(), true),
                        mShadowEnabled));
            } catch (RuntimeException rex) {
                Log.e(TAG, "RuntimeException " +rex.getMessage());
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            if (!TextUtils.isEmpty(mUrl)) {
                // if this view has pending request, retry again.
                String url = mUrl;
                mUrl = null;
                load(getContext(), url, null, null);
            }
        }
    }

    public void load(Context context, String url) {
        load(context, url, null, null);
    }

    public void load(Context context, String url, RequestListener listener, ScaleType scaleType) {
        if (url == null) {
            setDefaultDrawable();
            Glide.with(context).clear(this);
            return;
        }

        ViewGroup.LayoutParams param = getLayoutParams();
        int width = param != null && param.width > 0 ? param.width : getMeasuredWidth();
        int height = param != null && param.height > 0 ? param.height : getMeasuredHeight();
        if (width <= 0 || height <= 0) {
            mUrl = url;
            Log.i(TAG, getClass().getCanonicalName() + "must have specific size");
            return;
        }

        // Check if height is larger than Max_Texture_Size
        int maxHeight = ImageUtils.getMaxTexture();
        height = height > maxHeight ? maxHeight : height;

        if (ImageUtils.isLoadable(context, this)) {
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.bg_profile_image)
                    .transform(new CircleTransform(context))
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .override(width, height)
                    .circleCrop();

            Glide
                    .with(context)
                    .asBitmap()
                    .load(url)
                    .apply(requestOptions)
                    .listener(listener)
                    .into(this);
        }
    }
}
