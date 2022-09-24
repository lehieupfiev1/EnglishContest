package com.pfiev.englishcontest.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionItem implements Parcelable {
    public long qid;
    public String question;
    public ArrayList<ChoiceItem> listAnswer;
    public long answer;
    public int type;



    public QuestionItem(long qid, String question, ArrayList<ChoiceItem> listAnswer, long answer, int type) {
        this.qid = qid;
        this.question = question;
        this.listAnswer = listAnswer;
        this.answer = answer;
        this.type = type;
    }

    protected QuestionItem(Parcel in) {
        qid = in.readLong();
        question = in.readString();
        answer = in.readLong();
        type = in.readInt();
        listAnswer = new ArrayList<>();
        in.readTypedList(listAnswer,ChoiceItem.CREATOR);
    }

    public static final Creator<QuestionItem> CREATOR = new Creator<QuestionItem>() {
        @Override
        public QuestionItem createFromParcel(Parcel in) {
            return new QuestionItem(in);
        }

        @Override
        public QuestionItem[] newArray(int size) {
            return new QuestionItem[size];
        }
    };

    public void setQuestionId(int questionId) {
        this.qid = questionId;
    }

    public void setQuestionContent(String questionContent) {
        this.question = questionContent;
    }

    public void setListAnswer(ArrayList<ChoiceItem> listAnswer) {
        this.listAnswer = listAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.answer = correctAnswer;
    }


    public long getQuestionId() {
        return qid;
    }

    public String getQuestionContent() {
        return question;
    }

    public ArrayList<ChoiceItem> getListAnswer() {
        return listAnswer;
    }

    public long getCorrectAnswer() {
        return answer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(qid);
        parcel.writeString(question);
        parcel.writeLong(answer);
        parcel.writeInt(type);
        parcel.writeTypedList(listAnswer);
    }
}
