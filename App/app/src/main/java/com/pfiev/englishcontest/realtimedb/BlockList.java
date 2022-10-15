package com.pfiev.englishcontest.realtimedb;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.pfiev.englishcontest.model.BlockItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockList {
    private static BlockList instance;
    private final FirebaseDatabase dbCon;
    private final String blockListRef;
    private String uid;

    private boolean isListenBlockListStatus;
    private ChildEventListener blockStatusListener;

    public static BlockList getInstance() {
        if (instance == null) {
            instance = new BlockList();

            instance.isListenBlockListStatus = false;
        }
        return instance;
    }

    private BlockList() {
        this.dbCon = FirebaseDatabase.getInstance();
//        dbCon.useEmulator("192.168.1.127", 9000);
        this.blockListRef = "block_list";
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
     * Get block list order by time added
     *
     * @param listProcess list process
     */
    public void getListOrderByTime(Long startAt, Integer limit, BlockListProcess listProcess) {
        Query dbQuery = this.dbCon.getReference()
                .child(this.blockListRef).child(this.uid)
                .orderByChild(BlockItem.TIMESTAMP_FIELD_NAME);
        if (startAt == 0l) {
            dbQuery = dbQuery.startAt(startAt).limitToLast(limit);
        } else {
            dbQuery = dbQuery.endBefore(startAt).limitToLast(limit);
        }

        Log.d("Start AT", "" + startAt);

        dbQuery.get().addOnCompleteListener(
                task -> {
                    DataSnapshot dataSnap = task.getResult();
                    if (dataSnap != null) {
                        List<BlockItem> blockItemList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnap.getChildren()) {
                            blockItemList.add(snapshot.getValue(BlockItem.class));
                        }
                        Collections.reverse(blockItemList);
                        listProcess.process(blockItemList);
                    }
                }
        );
    }

    /**
     * Get list block search by name
     *
     * @param keyword
     * @param startAtName
     * @param limit
     * @param listProcess
     */
    public void getListSearchByName(
            String keyword, String startAtName, Integer limit,
            BlockListProcess listProcess) {
        String endAt = keyword + "\uf8ff";

        this.dbCon.getReference()
                .child(this.blockListRef).child(this.uid)
                .orderByChild(BlockItem.NAME_FIELD_NAME)
                .startAfter(startAtName)
                .endAt(endAt)
                .limitToFirst(limit).get().addOnCompleteListener(
                        task -> {
                            DataSnapshot dataSnap = task.getResult();
                            if (dataSnap != null) {
                                List<BlockItem> blockItemList = new ArrayList<>();
                                for (DataSnapshot snapshot : dataSnap.getChildren()) {
                                    blockItemList.add(snapshot.getValue(BlockItem.class));
                                }
                                // Fix sdk bug with startAfter
                                if (blockItemList.size() == 1 && keyword != startAtName) return;
                                listProcess.process(blockItemList);
                            }
                        }
                );
    }

    /**
     * Listen when block status change
     */
    public void listenBlockList(ItemEventListener itemEvent) {
        if (!isListenBlockListStatus) {
            isListenBlockListStatus = true;
            String blockRefWillCard = this.blockListRef + "/" + uid;
            DatabaseReference dbRef = this.dbCon.getReference().child(blockRefWillCard);
            blockStatusListener = new ChildEventListener() {
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
            dbRef.addChildEventListener(blockStatusListener);
        }
    }

    public void detachBlockStatusListener() {
        if (blockStatusListener != null) {
            String blockRefWillCard = this.blockListRef + "/" + uid;
            DatabaseReference dbRef = this.dbCon.getReference().child(blockRefWillCard);
            dbRef.removeEventListener(blockStatusListener);
        }
    }

    /**
     * Unblock user with uid
     *
     * @param uid
     */
    public void unBlockUser(String uid) {
        this.dbCon.getReference()
                .child(this.blockListRef).child(this.uid)
                .child(uid).removeValue();
    }

    /**
     * Add user to block
     *
     * @param blockUid
     * @param blockItem
     */
    public void addBlockUser(String blockUid, BlockItem blockItem) {
        this.dbCon.getReference()
                .child(this.blockListRef).child(this.uid)
                .child(blockUid).setValue(blockItem);
    }

    /**
     * Callback when get list user from gg
     */
    public static interface BlockListProcess {
        public void process(List<BlockItem> blockItemList);
    }

    public static interface ItemEventListener {
        public void onChildChanged(DataSnapshot snapshot);
    }

}
