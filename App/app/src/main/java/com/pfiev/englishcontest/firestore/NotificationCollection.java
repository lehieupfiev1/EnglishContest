package com.pfiev.englishcontest.firestore;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.pfiev.englishcontest.GlobalConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class NotificationCollection {
    public final static String COLLECTION_NAME = "m_notification";
    public static String SUB_COLLECTION = "list_notify";

    private CollectionReference colRef;
    private String ownUid;

    public NotificationCollection() {
        this.colRef = FirebaseFirestore.getInstance().collection(GlobalConstant.USERS);
    }

    /**
     * Set own id
     *
     * @param uid
     */
    public void setOwnUid(String uid) {
        ownUid = uid;
    }

    /**
     * Send notification
     *
     * @param friendId
     */
    public void sendFriendInvitation(String friendId) {
        JSONObject mainObject = new JSONObject();
        JSONObject messageObject = new JSONObject();
        try {
            messageObject.put("friendId", friendId);
            mainObject.put("message", messageObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        FirebaseFunctions.getInstance()
                .getHttpsCallable("addFriendInviteNotification")
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

}
