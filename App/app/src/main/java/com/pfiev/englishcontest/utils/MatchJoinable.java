package com.pfiev.englishcontest.utils;

import android.content.Context;

import java.time.Instant;

public class MatchJoinable {
    private static final String NOT_JOIN_MATCH_NUMBER = "not_join_match_number";
    private static final String BLOCK_JOIN_MATCH_FROM = "block_join_match_from";
    private static final int MAX_NOT_JOIN_NUMBER_CAL = 6;

    public static int getNotJoinMatchNumber(Context mContext) {
        return SharePreferenceUtils.getInt(mContext, NOT_JOIN_MATCH_NUMBER);
    }

    public static void resetNotJoinMatchNumber(Context mContext) {
        SharePreferenceUtils.putInt(mContext, NOT_JOIN_MATCH_NUMBER, 0);
    }

    public static void increaseNotJoinMatchNumber(Context mContext) {
        int number = SharePreferenceUtils.getInt(mContext, NOT_JOIN_MATCH_NUMBER) + 1;
        SharePreferenceUtils.putInt(mContext, NOT_JOIN_MATCH_NUMBER, number);
    }

    public static void setBlockFromNow(Context mContext) {
        SharePreferenceUtils.putLong(mContext,
                BLOCK_JOIN_MATCH_FROM, Instant.now().getEpochSecond());
    }

    /**
     * Get time remain block by second
     *
     * @param mContext context
     * @return time block remain in string
     */
    public static String getBlockTimeRemainString(Context mContext) {
        long blockFrom = SharePreferenceUtils.getLong(mContext, BLOCK_JOIN_MATCH_FROM);
        int number = getNotJoinMatchNumber(mContext);

        if ((blockFrom * number) == 0) return "";
        long blockTo;
        // if number larger than max not join cal then max block duration is 1 hour
        if (number > MAX_NOT_JOIN_NUMBER_CAL) {
            blockTo = blockFrom + 60000;
        } else blockTo = (long) (blockFrom + Math.pow(2, number - 1) * 60);
        long timeRemain = blockTo - Instant.now().getEpochSecond();
        if (timeRemain <= 0) return "";
        else if (timeRemain < 60) return timeRemain + "seconds";
        else return (timeRemain / 60) + " minutes and " + (timeRemain % 60) + " seconds";
    }
}
