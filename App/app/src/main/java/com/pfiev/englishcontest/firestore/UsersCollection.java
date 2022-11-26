package com.pfiev.englishcontest.firestore;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.pfiev.englishcontest.GlobalConstant;
import com.pfiev.englishcontest.model.BotItem;
import com.pfiev.englishcontest.model.UserItem;
import com.pfiev.englishcontest.model.content.UserFields;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class UsersCollection {
    private CollectionReference colRef;

    public UsersCollection() {
        this.colRef = FirebaseFirestore.getInstance().collection(GlobalConstant.USERS);
    }

    /**
     * Find user by name
     *
     * @param keyword    keyword
     * @param limit      number of return result
     * @param lastResult last result
     * @param findUserCb callback
     */
    public void findUsersByName
    (String keyword, int limit, String lastResult, FindUserCb findUserCb) {
        if (keyword.length() < 4) return;
        if (limit < 0) return;
        Query query;
        if (keyword.equals(lastResult)) {
            query = this.colRef.orderBy(UserFields.NAME).startAt(keyword);
        } else
            query = this.colRef.orderBy(UserFields.NAME).startAfter(lastResult);
        String endStr = keyword + "\uf8ff";
        query.endAt(endStr).limit(limit)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<UserItem> userItemList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        userItemList.add(documentSnapshot.toObject(UserItem.class));
                    }
                    if (userItemList.size() == 1 && !keyword.equals(lastResult)) return;
                    findUserCb.process(userItemList);
                });
    }

    public Task<UserItem> getBaseInfoByUid(String uid) {
        JSONObject mainObject = new JSONObject();
        JSONObject messageObject = new JSONObject();
        try {
            messageObject.put("uid", uid);
            mainObject.put("message", messageObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return FirebaseFunctions.getInstance()
                .getHttpsCallable("getBaseUserInfo")
                .call(mainObject)
                .continueWith(task -> {
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.

                    HashMap result = (HashMap) task.getResult().getData();
                    if (result == null) return null;
                    UserItem userItem = new UserItem();
                    userItem.setUserId((String) result.get("userId"));
                    userItem.setName((String) result.get("name"));
                    userItem.setUserPhotoUrl((String) result.get("userPhotoUrl"));
                    return userItem;
                });
    }

    public static Task<BotItem> useBotJoinMatch(String matchId) {
        JSONObject mainObject = new JSONObject();
        JSONObject messageObject = new JSONObject();
        try {
            messageObject.put("matchId", matchId);
            mainObject.put("message", messageObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return FirebaseFunctions.getInstance()
                .getHttpsCallable("useRandomBot")
                .call(mainObject)
                .continueWith(task -> {
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.

                    HashMap result = (HashMap) task.getResult().getData();
                    if (result == null) return null;
                    if (!(boolean) result.get("isSuccess")) return null;
                    HashMap botData = (HashMap) result.get("botData");
                    if (botData == null || botData.isEmpty()) return null;
                    BotItem botItem = new BotItem();
                    botItem.setUserId((String) botData.get("userId"));
                    botItem.setName((String) botData.get("name"));
                    botItem.setUserPhotoUrl((String) botData.get("userPhotoUrl"));
//                    botItem.setTrueAnswerRate();
                    HashMap botConfig = (HashMap) botData.get("botConfig");
                    if (botConfig == null) return null;
                    BotItem.BotConfig config = new BotItem.BotConfig();
                    config.setTrueAnswerRate(((Number) botConfig.get("trueAnswerRate")).doubleValue());
                    // Get speed answer rate
                    botConfig.get("speedAnswerRate");
                    int[] speedRate = new int[2];
                    ArrayList<Number> speedAnswerRate = (ArrayList<Number>)  botConfig.get("speedAnswerRate");
                    if (speedAnswerRate.size() ==2 ) {
                        speedAnswerRate.forEach(new Consumer<Number>() {
                            int i =0;
                            @Override
                            public void accept(Number aDouble) {
                                speedRate[i] = aDouble.intValue();
                                i++;
                            }
                        });
                    }
                    config.setSpeedAnswerRate(speedRate);

                    botItem.setBotConfig(config);

                    return botItem;
                });
    }

    public static interface FindUserCb {
        public void process(List<UserItem> userItemList);
    }
}
