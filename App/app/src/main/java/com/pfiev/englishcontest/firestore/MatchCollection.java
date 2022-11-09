package com.pfiev.englishcontest.firestore;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.pfiev.englishcontest.GlobalConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MatchCollection {
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
        public DeleteMatchResult(){}

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
                        new Continuation<HttpsCallableResult, DeleteMatchResult>() {
                            @Override
                            public DeleteMatchResult then(@NonNull Task<HttpsCallableResult> task) throws Exception {
//                                Log.d("Cancel find", task.getException().getMessage());
                                // This continuation runs on either success or failure, but if the task
                                // has failed then getResult() will throw an Exception which will be
                                // propagated down.
                                HashMap data = (HashMap) task.getResult().getData();
                                DeleteMatchResult result = new DeleteMatchResult();
                                result.code = (int) data.get(DEL_MATCH_RESULT_FIELD.CODE);
                                result.isSuccess = (boolean) data.get(DEL_MATCH_RESULT_FIELD.IS_SUCCESS);
                                return result;
                            }
                        });

    }
}
