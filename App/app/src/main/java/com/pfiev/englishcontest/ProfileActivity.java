package com.pfiev.englishcontest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.pfiev.englishcontest.databinding.ActivityProfileBinding;
import com.pfiev.englishcontest.firestore.FireStoreClass;
import com.pfiev.englishcontest.model.UserItem;
import com.pfiev.englishcontest.utils.ImageUtils;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;

import java.io.File;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private ActivityProfileBinding mBinding;
    private final int REQUEST_PERMISSION_CODE = 102;
    private final int REQUEST_PICK_FROM_ALBUM = 100;
    private final int ACTION_CROP_IMAGE_BY_GALLERY = 101;

    private static final double CROP_WIDTH = 320;
    private static final double AVATAR_HEIGHT = 320;
    private static Uri AVATAR_URI ;
    private static boolean isChangeImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.changeAvatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndGrantPermission();
            }
        });
        initData(this);
        mBinding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInfo(ProfileActivity.this);
            }
        });
        mBinding.radioMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBinding.radioFemale.isChecked()) {
                    mBinding.radioFemale.setChecked(false);
                }
                mBinding.radioMale.setChecked(true);
            }
        });
        mBinding.radioFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBinding.radioMale.isChecked()) {
                    mBinding.radioMale.setChecked(false);
                }
                mBinding.radioFemale.setChecked(true);
            }
        });

        mBinding.profileBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ExperimentalActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    public void saveUserInfo(Context mContext) {
        UserItem userItem = new UserItem();
        String userId = SharePreferenceUtils.getString(mContext,GlobalConstant.USER_ID);
        userItem.setUserId(userId);
        userItem.setName(mBinding.editPersonName.getText().toString());
        userItem.setEmail(mBinding.editEmail.getText().toString());
        userItem.setUserPhoneNumber(mBinding.editPhone.getText().toString());
        if (isChangeImage) {
            userItem.setUserPhotoUrl(AVATAR_URI.toString());
        } else {
            String userPhotoUrl = SharePreferenceUtils.getString(mContext,GlobalConstant.USER_PROFILE_IMAGE);
            userItem.setUserPhotoUrl(userPhotoUrl);
        }
        if (mBinding.radioMale.isChecked()) {
            userItem.setUserGender(GlobalConstant.USER_GENDER_MALE);
        } else if (mBinding.radioFemale.isChecked()) {
            userItem.setUserGender(GlobalConstant.USER_GENDER_FEMALE);
        }

        FireStoreClass.updateUserInfo(ProfileActivity.this,userItem);
    }

    public void saveUserProfileSuccess() {
        Toast.makeText(this, "saveUserProfileSuccess :", Toast.LENGTH_SHORT).show();
    }


    public void initData(Context mContext) {
        UserItem userItem = SharePreferenceUtils.getUserData(mContext);
        mBinding.avatarUser2.load(mContext,userItem.getUserPhotoUrl());
        mBinding.editPersonName.setText(userItem.getName());
        mBinding.editEmail.setText(userItem.getEmail());
        mBinding.editPhone.setText(userItem.getUserPhoneNumber());
        if (GlobalConstant.USER_GENDER_MALE.equalsIgnoreCase(userItem.getUserGender())) {
            mBinding.radioMale.setChecked(true);
        } else if (GlobalConstant.USER_GENDER_FEMALE.equalsIgnoreCase(userItem.getUserGender())) {
            mBinding.radioFemale.setChecked(true);
        }
    }

    private void checkAndGrantPermission() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, REQUEST_PERMISSION_CODE);
        } else {
            showChooseImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showChooseImage();
            } else {
                //Displaying another toast if permission is not granted
                //Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case REQUEST_PICK_FROM_ALBUM:
                if (resultCode == Activity.RESULT_OK) {
                    startCropActivityWithSelectedImage(intent);
                } else {
                    Toast.makeText(this, "Can not select image", Toast.LENGTH_SHORT).show();
                }
                break;
            case ACTION_CROP_IMAGE_BY_GALLERY:
                if (resultCode == Activity.RESULT_OK) {
                    updateAvatarImage();
                }
                break;

        }
    }

    protected void updateAvatarImage() {
        mBinding.avatarUser2.post(() -> {
            File file = new File(getExternalCacheDir()+ ImageUtils.AVATAR_TEMP_FILE);
            if (!file.exists() || file.isDirectory()) {
                Log.e(TAG, "Failed to access temp file, ");
                return;
            }

            Bitmap croppedBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (croppedBitmap == null) {
                Log.e(TAG, "Failed to decode cropped bitmap.");
                return;
            }

            mBinding.avatarUser2.setAdjustAvatarDrawable(croppedBitmap);
            FireStoreClass.uploadImageToCloudStorage(ProfileActivity.this, Uri.fromFile(file), "Avatar");

        });
    }

    public void uploadImageSuccess(Uri downloadUrl) {
        isChangeImage = true;
        Toast.makeText(this, "uploadImageSuccess :"+downloadUrl.toString(), Toast.LENGTH_SHORT).show();
        AVATAR_URI = downloadUrl;
    }

    public void showChooseImage() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            this.startActivityForResult(intent, REQUEST_PICK_FROM_ALBUM);
        } catch (Exception e) {
            Log.e(TAG,  e.getMessage(), e);
        }
    }

    private void startCropActivityWithSelectedImage(Intent intent) {
        Intent cropIntent = new Intent(getApplicationContext(), ProfileImageCropActivity.class);
        cropIntent.putExtra(GlobalConstant.CROP_RATIO_WIDTH, CROP_WIDTH);
        cropIntent.putExtra(GlobalConstant.CROP_RATIO_HEIGHT,AVATAR_HEIGHT);

        String path;
        float degree;

        Uri uri1 = intent.getData();
        path = ImageUtils.getFilepathFromContentUri(this, uri1);
        if (TextUtils.isEmpty(path)) {
            return;
        }
        degree = ImageUtils.getDegreeOfPath(path);

        Bitmap bitmap = ImageUtils.getBitmap(this, path, degree);
        String imagePath = ImageUtils.getTempPath(GlobalConstant.BEFORE_CROP_IMAGE,getBaseContext());
        ImageUtils.saveBitmap(bitmap, Bitmap.CompressFormat.JPEG, imagePath);

        cropIntent.putExtra(GlobalConstant.IMAGE_PATH, imagePath);

        // Create a temp file to receive cropped image uri
        // should use same uri in onActivityResult()
        File file = new File(getExternalCacheDir(), ImageUtils.AVATAR_TEMP_FILE);
        boolean success = ImageUtils.createIfNotExists(file);
        if (!success) return;

        Uri uri = Uri.fromFile(file);
        cropIntent.putExtra(GlobalConstant.KEY_CROPPED_OUTPUT_PATH, uri.getPath());

        startActivityForResult(cropIntent, ACTION_CROP_IMAGE_BY_GALLERY);

    }
}