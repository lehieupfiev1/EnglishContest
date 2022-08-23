package com.pfiev.englishcontest.ui.wiget;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.pfiev.englishcontest.GlobalConstant;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.utils.ImageUtils;
import com.theartofdev.edmodo.cropper.CropImageView;

public abstract class ImageCropActivity extends AppCompatActivity {

    protected static final String TAG = ImageCropActivity.class.getCanonicalName();

    private static final int MINIMUM_BITMAP_WIDTH_HEIGHT = 100;
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;

    // Instance variables
    protected int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    protected int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;
    protected String mImagePath;
    protected String mCroppedOutputPath;
    protected Bitmap mBitmap;

    protected CropImageView mCropImageView;

    // Saves the state upon rotating the screen/restarting the activity
    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putDouble(GlobalConstant.CROP_RATIO_WIDTH, mAspectRatioX);
        bundle.putDouble(GlobalConstant.CROP_RATIO_HEIGHT, mAspectRatioY);
        bundle.putString(GlobalConstant.IMAGE_PATH, mImagePath);
        bundle.putString(GlobalConstant.KEY_CROPPED_OUTPUT_PATH, mCroppedOutputPath);
    }

    protected abstract void setView();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView();
        initEditAppBar();

        mCropImageView = findViewById(R.id.CropImageView);
        //UiUtils.setAccessibilityForCustom(mCropImageView, getString(R.string.crop_image), getString(R.string.double_tap_and_hold_to_crop_image));
        mCropImageView.setGuidelines(CropImageView.Guidelines.ON);
        mCropImageView.setConners(CropImageView.Conners.ON);

        if (savedInstanceState != null) {
            mAspectRatioX = (int) savedInstanceState.getDouble(GlobalConstant.CROP_RATIO_WIDTH, 1);
            mAspectRatioY = (int) savedInstanceState.getDouble(GlobalConstant.CROP_RATIO_HEIGHT, 1);

            mImagePath = savedInstanceState.getString(GlobalConstant.IMAGE_PATH);
            mCroppedOutputPath = savedInstanceState.getString(GlobalConstant.KEY_CROPPED_OUTPUT_PATH);
        } else {
            Intent intent = getIntent();

            if (intent != null && intent.getExtras() != null) {
                mAspectRatioX = (int) intent.getExtras().getDouble(GlobalConstant.CROP_RATIO_WIDTH, 1);
                mAspectRatioY = (int) intent.getExtras().getDouble(GlobalConstant.CROP_RATIO_HEIGHT, 1);

                mImagePath = intent.getExtras().getString(GlobalConstant.IMAGE_PATH);
                mCroppedOutputPath = intent.getExtras().getString(GlobalConstant.KEY_CROPPED_OUTPUT_PATH);
            }
        }

        mCropImageView.setAspectRatio(mAspectRatioX, mAspectRatioY);
        mBitmap = BitmapFactory.decodeFile(mImagePath);
        if (mBitmap == null) {
            setResult(RESULT_CANCELED);
            finish();
        }

        if (mBitmap != null) {
            if (mBitmap.getWidth() < MINIMUM_BITMAP_WIDTH_HEIGHT || mBitmap.getHeight() < MINIMUM_BITMAP_WIDTH_HEIGHT) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        setImage(mBitmap);

    }

    @Override
    protected void onDestroy() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
        mBitmap = null;

        super.onDestroy();
    }

    public void setImage(Bitmap image) {
        if (mCropImageView != null) {
            mCropImageView.setImageBitmap(image);
        }
    }

    protected boolean saveCroppedImage() {
        Bitmap croppedBitmap = mCropImageView.getCroppedImage();
        if (croppedBitmap == null) return false;

        boolean success = ImageUtils.saveBitmap(croppedBitmap, Bitmap.CompressFormat.JPEG, mCroppedOutputPath);
        croppedBitmap.recycle();
        return success;
    }

    abstract protected Intent makeResultOKIntent();

    abstract public void initEditAppBar();
}

