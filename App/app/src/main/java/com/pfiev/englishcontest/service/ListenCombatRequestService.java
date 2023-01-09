package com.pfiev.englishcontest.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.pfiev.englishcontest.EnglishApplication;
import com.pfiev.englishcontest.GlobalConstant;
import com.pfiev.englishcontest.firestore.NotificationCollection;
import com.pfiev.englishcontest.model.BaseFriendListItem;
import com.pfiev.englishcontest.model.FriendItem;
import com.pfiev.englishcontest.model.NotificationItem;
import com.pfiev.englishcontest.ui.dialog.MakeFriendDialog;
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
        if (uid == null || uid.isEmpty()) return null;
        listenerRegistration = FirebaseFirestore.getInstance()
                .collection(NotificationCollection.COLLECTION_NAME)
                .document(uid).collection(NotificationCollection.SUB_COLLECTION)
                .whereEqualTo(NotificationItem.FIELD_NAME.HAS_READ, false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        // Check if allowed show notification
                        if (!EnglishApplication.isAllowedShowNotification()) return;

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                NotificationItem notificationItem =
                                        dc.getDocument().toObject(NotificationItem.class);
                                Map<String, Object> data = dc.getDocument().getData();
                                String type = data.get(NotificationItem.FIELD_NAME.TYPE).toString();
                                // Check time notification is expired
                                long timestamp = (long) data.get(
                                        NotificationItem.FIELD_NAME.TIMESTAMP);
                                if (timestamp > fromTime) {
                                    if (type.equals(NotificationItem.TYPE_VALUE.REQUEST_COMBAT)) {
                                        Handler handler = new Handler();
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                RequestCombatDialog dialog = new RequestCombatDialog(
                                                        EnglishApplication.getCurrentActivity());
                                                dialog.setData(notificationItem);
                                                dialog.show();
                                            }
                                        });
                                    }
                                    if (type.equals(NotificationItem.TYPE_VALUE.MAKE_FRIEND)) {
                                        Handler handler = new Handler();
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                MakeFriendDialog dialog = new MakeFriendDialog(
                                                        EnglishApplication.getCurrentActivity());
                                                dialog.setData(new FriendItem(
                                                        notificationItem.getUserId(),
                                                        notificationItem.getName(),
                                                        notificationItem.getUserPhotoUrl(),
                                                        BaseFriendListItem.STATUS.OFFLINE
                                                ));
                                                dialog.show();
                                            }
                                        });
                                    }
                                }
                                // Update notificationItem has read
                                dc.getDocument().getReference().update(
                                        NotificationItem.FIELD_NAME.HAS_READ,
                                        true
                                );
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

    public class ListenLocalBinder extends Binder {
        public ListenCombatRequestService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ListenCombatRequestService.this;
        }
    }
}
