package com.pfiev.englishcontest.model;

import java.util.ArrayList;

public class QuestionItem {
    public String questionId;
    public String questionContent;
    public ArrayList<String> listAnswer;
    public int correctAnswer;
    public int timeAnswer;

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public void setListAnswer(ArrayList<String> listAnswer) {
        this.listAnswer = listAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setTimeAnswer(int timeAnswer) {
        this.timeAnswer = timeAnswer;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public ArrayList<String> getListAnswer() {
        return listAnswer;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public int getTimeAnswer() {
        return timeAnswer;
    }
}
