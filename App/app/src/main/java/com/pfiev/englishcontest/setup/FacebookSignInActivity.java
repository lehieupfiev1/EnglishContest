package com.pfiev.englishcontest.setup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pfiev.englishcontest.ExperimentalActivity;
import com.pfiev.englishcontest.GlobalConstant;
import com.pfiev.englishcontest.LoginActivity;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.firestore.FireStoreClass;
import com.pfiev.englishcontest.model.UserItem;
import com.pfiev.englishcontest.ui.dialog.CustomToast;

import java.util.Arrays;


public class FacebookSignInActivity extends LoginActivity {
    private static final String TAG = "FacebookSignInActivity";

    private FirebaseAuth mAuth;

    private CallbackManager mCallbackManager;
    private LottieAnimationView loadingAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_sign_in);
        loadingAnim = findViewById(R.id.facebook_sign_in_activity_loading);

        showLoadingAnim();
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                LoginResult result = loginResult;
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.i(TAG, "facebook:onCancel");
                CustomToast.makeText(getApplicationContext(),
                        getString(R.string.login_error_notification),
                        CustomToast.ERROR, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FacebookSignInActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG, "facebook:onError", error);
                CustomToast.makeText(getApplicationContext(),
                        getString(R.string.login_error_notification),
                        CustomToast.ERROR, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FacebookSignInActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserItem userItem = new UserItem();
                            userItem.setUserId(user.getUid());
                            userItem.setName(user.getDisplayName());
                            userItem.setEmail(user.getEmail());
                            userItem.setUserPhotoUrl(user.getPhotoUrl().toString());
                            userItem.setUserPhoneNumber(user.getPhoneNumber());
                            userItem.setUserGender("");
                            FireStoreClass.registerUser(FacebookSignInActivity.this,
                                    userItem, GlobalConstant.FACEBOOK_ACCOUNT_TYPE);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                });
    }

    private void updateUI() {
        hideLoadingAnim();
        Log.i(TAG, "Navigate to MainActivity");
        Intent intent = new Intent(FacebookSignInActivity.this, ExperimentalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(GlobalConstant.ACCOUNT_TYPE,GlobalConstant.FACEBOOK_ACCOUNT_TYPE);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI();
        }

    }

    /**
     * Show loading animation
     */
    private void showLoadingAnim() {
        loadingAnim.setVisibility(View.VISIBLE);
        loadingAnim.playAnimation();
    }

    /**
     * Hide loading animation
     */
    private void hideLoadingAnim() {
        loadingAnim.setVisibility(View.INVISIBLE);
        loadingAnim.pauseAnimation();
    }

}
