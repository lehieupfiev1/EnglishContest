package com.pfiev.englishcontest.firestore;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pfiev.englishcontest.GlobalConstant;
import com.pfiev.englishcontest.model.UserItem;
import com.pfiev.englishcontest.model.content.UserFields;

import java.util.ArrayList;
import java.util.List;

public class UsersCollection {
    private CollectionReference colRef;

    public UsersCollection() {
        this.colRef = FirebaseFirestore.getInstance().collection(GlobalConstant.USERS);
    }

    public void findUsersByName
            (String keyword, int limit, String lastResult, FindUserCb findUserCb) {
        if (keyword.length() < 4) return;
        if (limit < 0) return;
//        if (lastResult.length() > 0) startStr = lastResult;\
        Query query;
        if (keyword.equals(lastResult)) {
            query = this.colRef.orderBy(UserFields.NAME).startAt(keyword);
        } else
            query = this.colRef.orderBy(UserFields.NAME).startAfter(lastResult);
        String endStr = keyword + "\uf8ff";
        query.endAt(endStr).limit(limit)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<UserItem> userItemList = new ArrayList<>();
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            userItemList.add(documentSnapshot.toObject(UserItem.class));
                        }
                        if(userItemList.size() == 1 && !keyword.equals(lastResult)) return;
                        findUserCb.process(userItemList);
                    }
                });
    }

    public static interface FindUserCb {
        public void process(List<UserItem> userItemList);
    }
}
