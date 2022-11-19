package com.pfiev.englishcontest.setup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.pfiev.englishcontest.GlobalConstant;
import com.pfiev.englishcontest.LoginActivity;
import com.pfiev.englishcontest.MainActivity;
import com.pfiev.englishcontest.PlayGameActivity;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.firestore.FireStoreClass;
import com.pfiev.englishcontest.model.UserItem;

public class TwitterSignInActivity extends LoginActivity {

    FirebaseAuth firebaseAuth;
    private static final String TAG = "TwitterSignInActivity";

    private LottieAnimationView loadingAnim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twiter_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");

        // Target specific email with login hint.
        provider.addCustomParameter("lang", "fr");
        showLoadingAnim();
        Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Log.i(TAG, "Twitter Sign-in onSuccess 1"+authResult.getAdditionalUserInfo().getProfile().toString());
                                    Toast.makeText(TwitterSignInActivity.this, "Login success : "+authResult.getAdditionalUserInfo().getProfile().toString(),Toast.LENGTH_SHORT);
                                    //startActivity(new Intent(TwitterSignInActivity.this, MainActivity.class));
                                    FirebaseUser user = authResult.getUser();
                                    UserItem userItem = new UserItem();
                                    userItem.setUserId(user.getUid());
                                    userItem.setName(user.getDisplayName());
                                    userItem.setUserPhotoUrl(user.getPhotoUrl().toString());
                                    userItem.setUserPhoneNumber(user.getPhoneNumber());
                                    userItem.setUserGender("");
                                    FireStoreClass.registerUser(TwitterSignInActivity.this,userItem, GlobalConstant.TWITTER_ACCOUNT_TYPE);
                                    //updateUI();
                                    hideLoadingAnim();

                                    // User is signed in.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile().
                                    // The OAuth access token can also be retrieved:
                                    // authResult.getCredential().getAccessToken().
                                    // The OAuth secret can be retrieved by calling:
                                    // authResult.getCredential().getSecret().
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(TAG, "Login fail 1 :" +e.getMessage());
                                    Toast.makeText(TwitterSignInActivity.this, "Login fail :" +e.getMessage(),Toast.LENGTH_SHORT);
                                    finish();
                                }
                            });
        } else {
            firebaseAuth
                    .startActivityForSignInWithProvider(/* activity= */ this, provider.build())
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Log.i(TAG, "Twitter Sign-in SignInWithProvider 2"+authResult.getUser().getDisplayName()+" ");
                                    Toast.makeText(TwitterSignInActivity.this, "Login success: "+authResult.getAdditionalUserInfo().getProfile().toString(),Toast.LENGTH_SHORT);
                                    FirebaseUser user = authResult.getUser();
                                    UserItem userItem = new UserItem();
                                    userItem.setUserId(user.getUid());
                                    userItem.setName(user.getDisplayName());
                                    userItem.setUserPhotoUrl(user.getPhotoUrl().toString());
                                    userItem.setUserPhoneNumber(user.getPhoneNumber());
                                    userItem.setUserGender("");
                                    FireStoreClass.registerUser(TwitterSignInActivity.this,userItem, GlobalConstant.TWITTER_ACCOUNT_TYPE);
                                    //updateUI();
                                    // User is signed in.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile().
                                    // The OAuth access token can also be retrieved:
                                    // authResult.getCredential().getAccessToken().
                                    // The OAuth secret can be retrieved by calling:
                                    // authResult.getCredential().getSecret().
                                    hideLoadingAnim();
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(TAG, "Login fail 2:" +e.getMessage());
                                    Toast.makeText(TwitterSignInActivity.this, "Login fail :" +e.getMessage(),Toast.LENGTH_SHORT);
                                    finish();
                                }
                            });
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