package com.pfiev.englishcontest.firestore;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.pfiev.englishcontest.GlobalConstant;
import com.pfiev.englishcontest.model.AnswerItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MatchCollection {
    public static final String COLLECTION_NAME = "match_history";
    public static final String CHOICES_FIELDS = "choices";
    public static final String LIST_CHOICE = "list_choice";

    public static class FIELD_NAME {
        public static final String CREATED_AT = "created_at";
    }

    public static class STATE {
        public static final String FINDING = "FINDING";
        public static final String WAITING_ACCEPT = "WAITING_ACCEPT";
        public static final String DUEL_ONE_JOIN = "DUEL_ONE_JOIN";
        public static final String PLAYING = "PLAYING";
        public static final String COMPLETE = "COMPLETE";

    }

    public static class DeleteMatchResult {
        public boolean isSuccess;
        public int code;
        public String message;

        public DeleteMatchResult() {
        }

    }

    public static final class DEL_MATCH_RESULT_FIELD {
        public static final String IS_SUCCESS = "isSuccess";
        public static final String CODE = "code";
    }

    public static final class DEL_MATCH_RESULT_CODE {
        public static final int IN_MATCHING = 402;
    }

    /**
     * Cancel looking match and delete match in finding state
     *
     * @param matchId match id
     * @return result of cancel find match
     */
    public static Task<DeleteMatchResult> cancelFindMatch(String matchId) {
        JSONObject mainObject = new JSONObject();
        JSONObject messageObject = new JSONObject();
        try {
            messageObject.put(GlobalConstant.MATCH_ID, matchId);
            mainObject.put("message", messageObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return FirebaseFunctions.getInstance()
                .getHttpsCallable("cancelFindMatch")
                .call(mainObject)
                .continueWith(
                        task -> {
//                                Log.d("Cancel find", task.getException().getMessage());
                            // This continuation runs on either success or failure, but if the task
                            // has failed then getResult() will throw an Exception which will be
                            // propagated down.
                            HashMap data = (HashMap) task.getResult().getData();
                            DeleteMatchResult result = new DeleteMatchResult();
                            result.code = (int) data.get(DEL_MATCH_RESULT_FIELD.CODE);
                            result.isSuccess = (boolean) data.get(DEL_MATCH_RESULT_FIELD.IS_SUCCESS);
                            return result;
                        });

    }

    /**
     * Update right answer from competitor
     *
     * @param matchId  match's id
     * @param userId   user id
     * @param callback callback
     */
    public static void updateDataOtherPlayer(String matchId, String userId, Callback callback) {
        FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(matchId)
                .collection(CHOICES_FIELDS).document(userId)
                .collection(LIST_CHOICE).whereEqualTo("is_right", true)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            long timeCorrect = (Long) dc.getDocument().getData().get("time_answer");
                            callback.run(timeCorrect);
                        }
                    }
                });
    }

    /**
     * Push answer to list choices
     * @param matchId id of match
     * @param userId owner user id
     * @param answerItem answer item to add
     * @param position order id of question
     */
    public static void pushAnswer(
            String matchId, String userId, AnswerItem answerItem, String position) {

        FirebaseFirestore.getInstance().collection(GlobalConstant.MATCH_HISTORY)
                .document(matchId)
                .collection(GlobalConstant.CHOICES).document(userId)
                .collection(GlobalConstant.LIST_CHOICE)
                .document(position).set(answerItem)
                .addOnSuccessListener(unused -> {
                })
                .addOnFailureListener(e -> {
                });
    }

    /**
     * Callback to call after update data
     */
    public interface Callback {
        /**
         * execute that
         * @param timeAnswer time choose
         */
        void run(long timeAnswer);
    }
}
