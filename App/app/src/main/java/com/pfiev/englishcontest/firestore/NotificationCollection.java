package com.pfiev.englishcontest.firestore;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pfiev.englishcontest.GlobalConstant;

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
     * @param uid
     */
    public void setOwnUid(String uid) {
        ownUid = uid;
    }

}
