package com.pfiev.englishcontest.realtimedb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.pfiev.englishcontest.model.FriendItem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class FriendList {
    private static FriendList instance;
    private final FirebaseDatabase dbCon;
    private final String friendListRef;
    private String uid;

    private boolean isListenFriendStatus;
    private ChildEventListener friendStatusListener;

    public static FriendList getInstance() {
        if (instance == null) {
            instance = new FriendList();
            instance.isListenFriendStatus = false;
        }
        return instance;
    }

    private FriendList() {
        this.dbCon = FirebaseDatabase.getInstance();
//        dbCon.useEmulator("192.168.1.127", 9000);
        this.friendListRef = "friends_list";
    }

    /**
     * Set owner uid
     *
     * @param uid owner uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Update change of user like status, avatar to friends
     *
     * @param listProcess list process
     */
    public void getListFriends(FriendListProcess listProcess) {
        DatabaseReference dbRef = this.dbCon.getReference()
                .child(this.friendListRef).child(this.uid);

        dbRef.get().addOnCompleteListener(
                task -> {
                    DataSnapshot dataSnap = task.getResult();
                    if (dataSnap != null) {
                        List<FriendItem> friendItemList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnap.getChildren()) {
                            friendItemList.add(snapshot.getValue(FriendItem.class));
                        }
                        // Sort by status priority
                        friendItemList.sort(new Comparator<FriendItem>() {
                            @Override
                            public int compare(FriendItem friendItem, FriendItem t1) {
                                int aPriority = FriendItem.STATUS.getPriority(friendItem.getStatus());
                                int bPriority = FriendItem.STATUS.getPriority(t1.getStatus());
                                return aPriority - bPriority;
                            }
                        });
                        listProcess.process(friendItemList);
                    }
                }
        );
    }

    /**
     * Search with name
     *
     * @param keyword
     * @param listProcess
     */
    public void getListFriendsWithName(String keyword, FriendListProcess listProcess) {
        if (keyword.isEmpty()) {
            getListFriends(listProcess);
            return;
        }
        Query dbRef = this.dbCon.getReference()
                .child(this.friendListRef).child(this.uid)
                .orderByChild(FriendItem.NAME_FIELD_NAME)
                .startAt(keyword).endAt(keyword + "\uf8ff");
        dbRef.get().addOnCompleteListener(
                task -> {
                    DataSnapshot dataSnap = task.getResult();
                    if (dataSnap != null) {
                        List<FriendItem> friendItemList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnap.getChildren()) {
                            friendItemList.add(snapshot.getValue(FriendItem.class));
                        }
                        listProcess.process(friendItemList);
                    }
                }
        );
    }

    /**
     * Update status to friend when online, offline or in playing match
     *
     * @param status user's status
     */
    public void updateStatusToFriends(String status) {
        this.getListFriends(new FriendListProcess() {
            private final String ownId = uid;

            @Override
            public void process(List<FriendItem> friendItemList) {
                Map<String, Object> updates = new HashMap<>();
                DatabaseReference ref = dbCon.getReference()
                        .child(friendListRef);
                // If has friend so update to they
                if (friendItemList != null)
                    if (!friendItemList.isEmpty()) {
                        for (FriendItem friendItem : friendItemList) {
                            // Create friend status ref to update
                            updates.put(
                                    getRef(friendItem.getUid(), FriendItem.STATUS_FIELD_NAME),
                                    status
                            );
                            updates.put(
                                    getRef(friendItem.getUid(), FriendItem.TIMESTAMP_FIELD_NAME),
                                    Instant.now().getEpochSecond()
                            );
                        }
                    }

                ref.updateChildren(updates);
            }

            public String getRef(String friendUid, String fieldName) {
                String statusRef = friendUid + "/" + ownId;
                return statusRef + "/" + fieldName;

            }
        });
    }

    /**
     * Listen when friend status change
     */
    public void listenFriendStatus(ItemEventListener itemEvent) {
        if (!isListenFriendStatus) {
            isListenFriendStatus = true;
            String friendRefWillCard = this.friendListRef + "/" + uid;
            DatabaseReference dbRef = this.dbCon.getReference().child(friendRefWillCard);
            friendStatusListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot,
                                         @Nullable String previousChildName) {

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot,
                                           @Nullable String previousChildName) {
                    if (snapshot.exists()) {
                        itemEvent.onChildChanged(snapshot);
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot,
                                         @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            dbRef.addChildEventListener(friendStatusListener);
        }
    }

    /**
     * Detach listener friend's status
     */
    public void detachFriendStatusListener() {
        if (friendStatusListener != null) {
            String friendRefWillCard = this.friendListRef + "/" + uid;
            DatabaseReference dbRef = this.dbCon.getReference().child(friendRefWillCard);
            dbRef.removeEventListener(friendStatusListener);
        }
    }

    /**
     * Delete friend have uid
     * @param ownId
     * @param friendUid
     */
    public void deleteFriend(String ownId, String friendUid) {
        this.dbCon.getReference()
                .child(this.friendListRef).child(ownId)
                .child(friendUid).removeValue();
        this.dbCon.getReference()
                .child(this.friendListRef).child(friendUid)
                .child(ownId).removeValue();
    }

    /**
     * Add user to friend list
     *
     * @param friends Map with key is user's id and value is data to insert
     *
     */
    public void addFriend(Map<String, FriendItem> friends) {
        Map <String, Object> data = new HashMap<>();
        friends.forEach(new BiConsumer<String, FriendItem>() {
            @Override
            public void accept(String s, FriendItem friendItem) {
                data.put("/" + friendListRef + "/" + s + "/" + friendItem.getUid(), friendItem);
            }
        });
        dbCon.getReference().updateChildren(data);
    }

    public static interface FriendListProcess {
        public void process(List<FriendItem> friendItemList);
    }

    public static interface ItemEventListener {
        public void onChildChanged(DataSnapshot snapshot);
    }
}
