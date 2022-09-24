package com.pfiev.englishcontest.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ChoiceItem implements Parcelable {
    public long id;
    public String content;

    public ChoiceItem(long id, String content) {
        this.id = id;
        this.content = content;
    }

    protected ChoiceItem(Parcel in) {
        id = in.readLong();
        content = in.readString();
    }

    public static final Creator<ChoiceItem> CREATOR = new Creator<ChoiceItem>() {
        @Override
        public ChoiceItem createFromParcel(Parcel in) {
            return new ChoiceItem(in);
        }

        @Override
        public ChoiceItem[] newArray(int size) {
            return new ChoiceItem[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(content);
    }
}
