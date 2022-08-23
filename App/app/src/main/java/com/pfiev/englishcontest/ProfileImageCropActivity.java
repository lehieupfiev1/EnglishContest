package com.pfiev.englishcontest;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pfiev.englishcontest.ui.wiget.ImageCropActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ProfileImageCropActivity extends ImageCropActivity {

    protected static final String TAG = ProfileImageCropActivity.class.getCanonicalName();

    @Override
    protected void setView() {
        setContentView(R.layout.activity_profile_image_crop);
        int nightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if(nightMode == Configuration.UI_MODE_NIGHT_NO) {
            changeColorBar();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mAspectRatioX == mAspectRatioY) {
            mCropImageView.setCropShape(CropImageView.CropShape.OVAL);
        }

        mCropImageView.setGuidelines(CropImageView.Guidelines.OFF);

    }

    @Override
    protected Intent makeResultOKIntent() {
        return new Intent();
    }

    @Override
    public void initEditAppBar() {
        BottomNavigationView mBottomBar = findViewById(R.id.bottom_bar);
        mBottomBar.setOnItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.cancel:
                    onBackPressed();
                    break;

                case R.id.save:
                    boolean isSaveSuccessful = saveCroppedImage();
                    if (isSaveSuccessful) {
                        setResult(Activity.RESULT_OK, makeResultOKIntent());
                    } else {
                        setResult(Activity.RESULT_CANCELED);
                    }
                    finish();
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    public void changeColorBar(){
        Window window = getWindow();
        int color = getColor(R.color.black);
        window.setStatusBarColor(color);
        window.setNavigationBarColor(color);
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}
