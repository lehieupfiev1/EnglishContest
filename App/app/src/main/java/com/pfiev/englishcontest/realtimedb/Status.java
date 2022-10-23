package com.pfiev.englishcontest.realtimedb;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;

public class Status {
    private static Status instance;
    private FirebaseDatabase database;
    private String statusRef;
    private String ownUid;
    private ValueEventListener listener;
    private DatabaseReference connectedRef;

    private final String TAG = "Status Db";
    private final String STATE_ONLINE = "online";
    private final String STATE_OFFLINE = "offline";

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
                    ownStatusRef.get().addOnSuccessListener(
                            new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    StatusModel status = null;
                                    if (dataSnapshot != null && dataSnapshot.exists()) {
                                        status = dataSnapshot.getValue(Status.StatusModel.class);
                                    }
                                    if (status == null || status.state.equals(STATE_OFFLINE)) {
                                        myStateRef.onDisconnect().setValue(STATE_OFFLINE);
                                        // When I disconnect, update the last time I was seen online
                                        lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                                        // Set state online and device hash
                                        hashDisplayRef.setValue(deviceHash);
                                        // Set value is online now
                                        myStateRef.setValue(STATE_ONLINE);
                                    } else {
                                        if (!status.hashDisplay.equals(deviceHash)) {
                                            stateCallBack.notSameHashCallback();
                                            connectedRef.removeEventListener(listener);
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

    public static class StatusModel {
        public String state;
        public String hashDisplay;
        public float lastOnline;

        public StatusModel() {
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getHashDisplay() {
            return hashDisplay;
        }

        public void setHashDisplay(String hashDisplay) {
            this.hashDisplay = hashDisplay;
        }

        public float getLastOnline() {
            return lastOnline;
        }

        public void setLastOnline(float lastOnline) {
            this.lastOnline = lastOnline;
        }

    }

    public static interface StateCallBack {
        public void notSameHashCallback();
    }
}
