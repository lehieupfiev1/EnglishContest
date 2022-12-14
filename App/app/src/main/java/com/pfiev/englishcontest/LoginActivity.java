package com.pfiev.englishcontest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pfiev.englishcontest.databinding.ActivityLoginBinding;
import com.pfiev.englishcontest.setup.FacebookSignInActivity;
import com.pfiev.englishcontest.setup.GoogleSignInActivity;
import com.pfiev.englishcontest.setup.TwitterSignInActivity;
import com.pfiev.englishcontest.ui.dialog.CustomToast;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding mBinding;
    public ProgressBar mProgressBar;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        AppEventsLogger.activateApp(getApplication());

        mBinding.signInFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Navigate to FaceBookSignIn Activity");
                Intent intent = new Intent(LoginActivity.this, FacebookSignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        mBinding.signInGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Navigate to GoogleSignIn Activity");
                Intent intent = new Intent(LoginActivity.this, GoogleSignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        mBinding.signInTwitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Navigate to TwitterSignIn Activity");
                Intent intent = new Intent(LoginActivity.this, TwitterSignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        setProgressBar(mBinding.progressBar);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            CustomToast.makeText(getApplicationContext(),
                    getString(R.string.login_success_notification),
                    CustomToast.SUCCESS,CustomToast.LENGTH_SHORT);
            Intent intent = new Intent(LoginActivity.this, ExperimentalActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Log.i(TAG, "Navigate to MainActivity");
        }
    }

    public void setProgressBar(ProgressBar progressBar) {
        mProgressBar = progressBar;
    }

    public void showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

}