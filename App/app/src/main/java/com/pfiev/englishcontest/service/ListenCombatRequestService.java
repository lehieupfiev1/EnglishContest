package com.pfiev.englishcontest.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.pfiev.englishcontest.EnglishApplication;
import com.pfiev.englishcontest.GlobalConstant;
import com.pfiev.englishcontest.firestore.NotificationCollection;
import com.pfiev.englishcontest.model.NotificationItem;
import com.pfiev.englishcontest.ui.dialog.RequestCombatDialog;

import java.time.Instant;
import java.util.Map;

public class ListenCombatRequestService extends Service {

    private String uid;
    private String TAG = "Listen service";
    private ListenerRegistration listenerRegistration;
    private long timeDistance = 60;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        FirebaseApp.initializeApp(getApplicationContext());
        long fromTime = Instant.now().getEpochSecond() - timeDistance;
        uid = intent.getExtras().getString(GlobalConstant.USER_ID);
        listenerRegistration = FirebaseFirestore.getInstance()
                .collection(NotificationCollection.COLLECTION_NAME)
                .document(uid).collection(NotificationCollection.SUB_COLLECTION)
                .whereEqualTo(NotificationItem.FIELD_NAME.HAS_READ, false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                NotificationItem notificationItem =
                                        dc.getDocument().toObject(NotificationItem.class);
                                Map<String, Object> data = dc.getDocument().getData();
                                String type = data.get(NotificationItem.FIELD_NAME.TYPE).toString();
                                if (type.equals(NotificationItem.TYPE_VALUE.REQUEST_COMBAT)) {
                                    long timestamp = (long) data.get(
                                            NotificationItem.FIELD_NAME.TIMESTAMP);
                                    if (timestamp > fromTime) {
                                        Handler handler = new Handler();
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                RequestCombatDialog dialog = new RequestCombatDialog(
                                                        EnglishApplication.getCurrentActivity());
                                                dialog.setData(getBundleData(notificationItem));
                                                dialog.show();
                                            }
                                        });
                                    }
                                    // Update notificationItem has read
                                    dc.getDocument().getReference().update(
                                            NotificationItem.FIELD_NAME.HAS_READ,
                                            true
                                    );
                                }
                            }
                        }
                    }
                });
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        listenerRegistration.remove();
        return super.onUnbind(intent);
    }

    private Bundle getBundleData(NotificationItem notificationItem) {
        Bundle bundle = new Bundle();
        bundle.putString(
                NotificationItem.FIELD_NAME.USER_NAME,
                notificationItem.getName()
        );
        bundle.putString(
                NotificationItem.FIELD_NAME.MATCH_ID,
                notificationItem.getMatchId()
        );
        bundle.putString(
                NotificationItem.FIELD_NAME.USER_PHOTO_URL,
                notificationItem.getUserPhotoUrl()
        );
        bundle.putString(
                NotificationItem.FIELD_NAME.TYPE,
                notificationItem.getType()
        );
        return bundle;
    }

    public class ListenLocalBinder extends Binder {
        public ListenCombatRequestService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ListenCombatRequestService.this;
        }
    }
}
