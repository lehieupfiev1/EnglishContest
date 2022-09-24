package com.pfiev.englishcontest.firestore;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pfiev.englishcontest.ExperimentalActivity;
import com.pfiev.englishcontest.GlobalConstant;
import com.pfiev.englishcontest.LoginActivity;
import com.pfiev.englishcontest.MainActivity;
import com.pfiev.englishcontest.PlayGameActivity;
import com.pfiev.englishcontest.ProfileActivity;
import com.pfiev.englishcontest.model.AnswerItem;
import com.pfiev.englishcontest.model.UserItem;
import com.pfiev.englishcontest.setup.GoogleSignInActivity;
import com.pfiev.englishcontest.utils.ImageUtils;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class FireStoreClass {
    //private static FireStoreClass mFireStoreClass = null;
    public static String TAG  = "FireStoreClass";
//    public FireStoreClass getInstance() {
//        if (mFireStoreClass == null) {
//            mFireStoreClass = new FireStoreClass();
//        }
//       return mFireStoreClass;
//    }

    public FirebaseFirestore mFireStore = FirebaseFirestore.getInstance() ;


    //Create User in FireStore
    public static void registerUser (Activity activity, UserItem userInfo, int typeAccount) {
        DocumentReference user = FirebaseFirestore.getInstance().collection(GlobalConstant.USERS)
                .document(userInfo.getUserId());
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i(TAG, "registerUser Document exists!"+document.toString());
                        UserItem userItem = document.toObject(UserItem.class);
                        SharePreferenceUtils.updateUserData(activity.getApplicationContext(),userItem);
                        Toast.makeText(activity, "UserId"+userItem.getUserId() + " UserName" +userItem.getName()+" UserPhoto"+userItem.getUserPhotoUrl(), Toast.LENGTH_SHORT).show();
                        navigateToMainActivity(activity,typeAccount);

                    } else {
                        Log.i(TAG, "registerUser Document does not exist!");
                        user.set(userInfo, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //Success create User
                                        SharePreferenceUtils.updateUserData(activity.getApplicationContext(),userInfo);
                                        navigateToMainActivity(activity,typeAccount);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(activity,"Can't register Account",Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Failed with: "+ e.getMessage());
                                    }
                                });
                    }
                } else {
                    Log.e(TAG, "Failed with: ", task.getException());
                }
            }
        });
    }

    public static void navigateToMainActivity(Activity activity, int typeAccount) {
        Log.i(TAG, "Navigate to MainActivity "+activity.toString());
        Intent intent = new Intent(activity, ExperimentalActivity.class);
        intent.putExtra(GlobalConstant.ACCOUNT_TYPE,typeAccount);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void updateUserInfo(Activity activity,UserItem userInfo) {
        DocumentReference user = FirebaseFirestore.getInstance().collection(GlobalConstant.USERS)
                .document(userInfo.getUserId());
        user.set(userInfo,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //Success create User
                Log.i(TAG, "updateUserInfo  onSuccess");
                SharePreferenceUtils.updateUserData(activity.getApplicationContext(),userInfo);
                if (activity instanceof ProfileActivity) {
                    ((ProfileActivity) activity).saveUserProfileSuccess();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity,"Can't register Account",Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed with: "+ e.getMessage());
            }
        });

    }

    public static void pushAnswer(Activity activity, String matchId, String userId, AnswerItem answerItem, String position) {

        FirebaseFirestore.getInstance().collection(GlobalConstant.MATCH_HISTORY).document(matchId)
                .collection(GlobalConstant.CHOICES).document(userId)
                .collection(GlobalConstant.LIST_CHOICE).document(position).set(answerItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(TAG, "pushAnswer answer "+position);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public static void updateDataOtherPlayer(Activity activity,String matchId, String userId) {
        FirebaseFirestore.getInstance().collection(GlobalConstant.MATCH_HISTORY).document(matchId)
                .collection(GlobalConstant.CHOICES).document(userId)
                .collection(GlobalConstant.LIST_CHOICE).whereEqualTo("is_right",true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "listen:error", error);
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                Log.i(TAG, "New answer is right: " + dc.getDocument().getData());
                                long timeCorrect = (Long) dc.getDocument().getData().get("time_answer");
                                if (activity instanceof PlayGameActivity) {
                                    Log.i(TAG, "updateDataOtherPlayer PlayGameActivity "+timeCorrect);
                                    ((PlayGameActivity) activity).updateDataOtherPlayerInfo(timeCorrect);
                                }
                            }
                        }
                    }
                });
    }

    public static void requestUserInfo(Activity activity, String useId) {
        DocumentReference user = FirebaseFirestore.getInstance().collection(GlobalConstant.USERS)
                .document(useId);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i(TAG, "registerUser Document exists!" + document.toString());
                        UserItem userItem = document.toObject(UserItem.class);
                        if (activity instanceof PlayGameActivity) {
                            Log.i(TAG, "registerUser PlayGameActivity "+userItem.getUserPhotoUrl());
                            ((PlayGameActivity) activity).getUserProfileSuccess(userItem);
                        }
                    }
                }
            }
        });
    }

    public static Task<String> findMatchRequest(String uid) {
        // Create the arguments to the callable function.
        JSONObject mainObject = new JSONObject();
        JSONObject messageObject = new JSONObject();
        try {
            messageObject.put(GlobalConstant.UID, uid);
            mainObject.put("message", messageObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "findMatchRequest  "+uid+ mainObject.toString());

        return FirebaseFunctions.getInstance()
                .getHttpsCallable("findMatch")
                .call(mainObject)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        HashMap result = (HashMap) task.getResult().getData();
                        return result.toString();
                    }
                });
    }

    public static Task<String> joinMatchRequest(String uid, String matchId) {
        JSONObject mainObject = new JSONObject();
        JSONObject messageObject = new JSONObject();
        try {
            messageObject.put(GlobalConstant.UID, uid);
            messageObject.put(GlobalConstant.MATCH_ID, matchId);
            messageObject.put(GlobalConstant.ACTION, "ACCEPT");
            mainObject.put("message", messageObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "joinMatchRequest  "+uid+ mainObject.toString());

        return FirebaseFunctions.getInstance()
                .getHttpsCallable("joinMatch")
                .call(mainObject)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        HashMap result = (HashMap) task.getResult().getData();
                        return result.toString();
                    }
                });
    }

    public static void uploadImageToCloudStorage( Activity activity, Uri imageUri, String imageType) {
        Log.i(TAG, "uploadImageToCloudStorage ");
        final StorageReference storageReference = FirebaseStorage.getInstance("gs://pfiev-english-contest-01.appspot.com").getReference().child(imageType + System.currentTimeMillis() + "."
                + ".jpg");

        Task<Uri> urlTask = storageReference.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    if (activity instanceof ProfileActivity) {
                        ((ProfileActivity) activity).uploadImageSuccess(downloadUri);
                    }
                } else {
                    Log.e(TAG, "Upload fail: ");
                }
            }
        });
    }

}
