package com.pfiev.englishcontest.realtimedb;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;

public class Status {
    private static Status instance;
    private FirebaseDatabase database;
    private String statusRef;
    private String ownUid;
    private ValueEventListener listener;
    private DatabaseReference connectedRef;

    private final String TAG = "Status Db";
    public static String STATE_ONLINE = "online";
    public static String STATE_OFFLINE = "offline";
    public static String STATE_PLAYING = "playing";

    private static final int MAX_FRIENDS_ALLOW = 50;

    public static Status getInstance() {
        if (instance == null) {
            instance = new Status();
        }
        return instance;
    }

    private Status() {
        this.database = FirebaseDatabase.getInstance();
//        database.useEmulator("192.168.1.127", 9000);
        this.statusRef = "status";
    }

    public void setOwnUid(String ownUid) {
        this.ownUid = ownUid;
    }

    /**
     * Set event when connect or disconnect
     */
    public void setOnDisconnectAction(Activity activity, StateCallBack stateCallBack) {

        DatabaseReference ownStatusRef = database.getReference()
                .child(this.statusRef + "/" + this.ownUid);
        String deviceHash = SharePreferenceUtils.getDeviceDisplayHash(activity);

        final DatabaseReference myStateRef = ownStatusRef.child("state");
        final DatabaseReference lastOnlineRef = ownStatusRef.child("lastOnline");
        final DatabaseReference hashDisplayRef = ownStatusRef.child("hashDisplay");
        connectedRef = database.getReference(".info/connected");
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    ownStatusRef.get().addOnCompleteListener(
                            new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Exception e = task.getException();
                                        if (e instanceof FirebaseFunctionsException) {
                                            FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                            FirebaseFunctionsException.Code code = ffe.getCode();
                                            Object details = ffe.getDetails();
                                        }
                                        Log.i(TAG, " task is not success :" + e.getMessage());
                                    } else {
                                        StatusModel status = null;
                                        DataSnapshot dataSnapshot = task.getResult();
                                        if (dataSnapshot != null && dataSnapshot.exists()) {
                                            status = dataSnapshot.getValue(Status.StatusModel.class);
                                        }
                                        if (status == null || status.state.equals(STATE_OFFLINE)) {
                                            myStateRef.onDisconnect().setValue(STATE_OFFLINE);
                                            // When I disconnect, update the last time I was seen online
                                            lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                                            // Set new timestamp
                                            lastOnlineRef.setValue(ServerValue.TIMESTAMP);
                                            // Set state online and device hash
                                            hashDisplayRef.setValue(deviceHash);
                                            // Set value is online now
                                            myStateRef.setValue(STATE_ONLINE);
                                            stateCallBack.afterSetOnDisconnectAction();
                                        } else {
                                            if (!status.hashDisplay.equals(deviceHash)) {
                                                connectedRef.removeEventListener(listener);
                                                stateCallBack.notSameHashCallback();
                                            } else {
                                                myStateRef.onDisconnect().setValue(STATE_OFFLINE);
                                                // When I disconnect, update the last time I was seen online
                                                lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                                            }
                                        }
                                    }
                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Listener was cancelled at .info/connected");
            }
        };
        connectedRef.addValueEventListener(listener);
    }

    public void destroyListener() {
        connectedRef.removeEventListener(listener);
    }

    /**
     * Get status state of user by uid
     *
     * @param uid
     * @return
     */
    public Task<String> getStatusByUid(String uid) {
        DatabaseReference friendStatusRef = database.getReference()
                .child(this.statusRef + "/" + uid).child("state");
        return friendStatusRef.get().continueWith(new Continuation<DataSnapshot, String>() {

            @Override
            public String then(@NonNull Task<DataSnapshot> task) throws Exception {
                return task.getResult().getValue().toString();
            }
        });
    }

    public Task<Boolean> canAddNewFriend() {
        return database.getReference().child(this.statusRef + "/" + this.ownUid)
                .child("totalFriends")
                .get().continueWith(new Continuation<DataSnapshot, Boolean>() {
                    @Override
                    public Boolean then(@NonNull Task<DataSnapshot> task) throws Exception {
                        long totalFriends = 0;
                        if (task.getResult().getValue() != null)
                            totalFriends = (long) task.getResult().getValue();
                        return (totalFriends < MAX_FRIENDS_ALLOW);
                    }
                });
    }

    /**
     * Set new state
     *
     * @param newState new state
     */
    public void setState(String newState) {
        database.getReference().child(this.statusRef + "/" + this.ownUid).child("state")
                .setValue(newState);
    }

    public static class StatusModel {
        public String state;
        public String hashDisplay;
        public float lastOnline;
        public long totalFriends;

        public StatusModel() {
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

    }

    public static interface StateCallBack {
        public void afterSetOnDisconnectAction();

        public void notSameHashCallback();
    }
}
