package com.pfiev.englishcontest.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BotItem extends UserItem{
    BotConfig botConfig;

    public BotItem() {
    }

    public BotItem(
            String userName, String userId, String userEmail,
            String userPhotoUrl, String userPhoneNumber, String userGender,
            BotConfig botConfig) {
        this.userName = userName;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userPhotoUrl = userPhotoUrl;
        this.userPhoneNumber = userPhoneNumber;
        this.userGender = userGender;
        this.botConfig = botConfig;
    }

    protected BotItem(Parcel in) {
        userName = in.readString();
        userId = in.readString();
        userEmail = in.readString();
        userPhotoUrl = in.readString();
        userPhoneNumber = in.readString();
        userGender = in.readString();
        botConfig = in.readTypedObject(BotConfig.CREATOR);
    }

    public static final Creator<BotItem> CREATOR = new Creator<BotItem>() {
        @Override
        public BotItem createFromParcel(Parcel in) {
            return new BotItem(in);
        }

        @Override
        public BotItem[] newArray(int size) {
            return new BotItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userName);
        parcel.writeString(userId);
        parcel.writeString(userEmail);
        parcel.writeString(userPhotoUrl);
        parcel.writeString(userPhoneNumber);
        parcel.writeString(userGender);
        parcel.writeParcelable(botConfig,PARCELABLE_WRITE_RETURN_VALUE);
    }


    public BotConfig getBotConfig() {
        return botConfig;
    }

    public void setBotConfig(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    public static class BotConfig implements Parcelable {
        double trueAnswerRate;
        int[] speedAnswerRate;

        public BotConfig() {}

        public BotConfig(double trueAnswerRate, int[] speedAnswerRate) {
            this.trueAnswerRate = trueAnswerRate;
            this.speedAnswerRate = speedAnswerRate;
        }

        protected BotConfig(Parcel in) {
            trueAnswerRate = in.readDouble();
            speedAnswerRate = in.createIntArray();
        }

        public static final Creator<BotConfig> CREATOR = new Creator<BotConfig>() {
            @Override
            public BotConfig createFromParcel(Parcel in) {
                return new BotConfig();
            }

            @Override
            public BotConfig[] newArray(int size) {
                return new BotConfig[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeDouble(trueAnswerRate);
            parcel.writeIntArray(speedAnswerRate);
        }


        public double getTrueAnswerRate() {
            return trueAnswerRate;
        }

        public void setTrueAnswerRate(double trueAnswerRate) {
            this.trueAnswerRate = trueAnswerRate;
        }

        public int[] getSpeedAnswerRate() {
            return speedAnswerRate;
        }

        public void setSpeedAnswerRate(int[] speedAnswerRate) {
            this.speedAnswerRate = speedAnswerRate;
        }
    }
}
