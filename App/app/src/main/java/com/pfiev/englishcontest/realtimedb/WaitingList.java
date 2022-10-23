package com.pfiev.englishcontest.realtimedb;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.pfiev.englishcontest.model.WaitingItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WaitingList {
    private static WaitingList instance;
    private final FirebaseDatabase dbCon;
    private final String waitingListRef;
    private String uid;


    public static WaitingList getInstance() {
        if (instance == null) {
            instance = new WaitingList();
        }
        return instance;
    }

    private WaitingList() {
        this.dbCon = FirebaseDatabase.getInstance();
//        dbCon.useEmulator("192.168.1.127", 9000);
        this.waitingListRef = "waiting_list";
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
     * Get waiting list order by time added
     *
     * @param listProcess list process
     */
    public void getListOrderByTime(Long startAt, Integer limit, WaitingListProcess listProcess) {
        Query dbQuery = this.dbCon.getReference()
                .child(this.waitingListRef).child(this.uid)
                .orderByChild(WaitingItem.TIMESTAMP_FIELD_NAME);
        if (startAt == 0l) {
            dbQuery = dbQuery.startAt(startAt).limitToLast(limit);
        } else {
            dbQuery = dbQuery.endBefore(startAt).limitToLast(limit);
        }

        dbQuery.get().addOnCompleteListener(
                task -> {
                    DataSnapshot dataSnap = task.getResult();
                    if (dataSnap != null) {
                        List<WaitingItem> waitingItemList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnap.getChildren()) {
                            waitingItemList.add(snapshot.getValue(WaitingItem.class));
                        }
                        Collections.reverse(waitingItemList);
                        listProcess.process(waitingItemList);

                    }
                }
        );
    }

    /**
     * Get list waiting search by name
     *
     * @param keyword
     * @param startAtName
     * @param limit
     * @param listProcess
     */
    public void getListSearchByName(
            String keyword, String startAtName, Integer limit,
            WaitingListProcess listProcess) {
        String endAt = keyword + "\uf8ff";

        this.dbCon.getReference()
                .child(this.waitingListRef).child(this.uid)
                .orderByChild(WaitingItem.NAME_FIELD_NAME)
                .startAfter(startAtName)
                .endAt(endAt)
                .limitToFirst(limit).get().addOnCompleteListener(
                        task -> {
                            DataSnapshot dataSnap = task.getResult();
                            if (dataSnap != null) {
                                List<WaitingItem> waitingItemList = new ArrayList<>();
                                for (DataSnapshot snapshot : dataSnap.getChildren()) {
                                    waitingItemList.add(snapshot.getValue(WaitingItem.class));
                                }
                                // Fix sdk bug with startAfter
                                if (waitingItemList.size() == 1 && keyword != startAtName) return;
                                listProcess.process(waitingItemList);
                            }
                        }
                );
    }

    /**
     * Delete invitation after accept or reject
     *
     * @param fromUid
     */
    public void deleteInvitation(String fromUid) {
        this.dbCon.getReference()
                .child(this.waitingListRef).child(this.uid)
                .child(fromUid).removeValue();
    }

    /**
     * Send Invitation to user
     *
     * @param toUid
     * @param waitingItem
     */
    public Task<Void> sendInvitation(String toUid, WaitingItem waitingItem) {
        return this.dbCon.getReference()
                .child(this.waitingListRef).child(toUid)
                .child(this.uid).setValue(waitingItem);
    }

    /**
     * Call back after get list waiting user
     */
    public static interface WaitingListProcess {
        public void process(List<WaitingItem> waitingItemList);
    }

    public static interface ItemEventListener {
        public void onChildChanged(DataSnapshot snapshot);
    }

}
