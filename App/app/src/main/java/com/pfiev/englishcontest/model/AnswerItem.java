package com.pfiev.englishcontest.model;

public class AnswerItem {
    private long question_id;
    private long choice_id;
    private int time_answer;
    private boolean is_right;

    public AnswerItem() {
    }

    public AnswerItem(long question_id, long choice_id, int time_answer, boolean is_right) {
        this.question_id = question_id;
        this.choice_id = choice_id;
        this.time_answer = time_answer;
        this.is_right = is_right;
    }

    public long getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(long question_id) {
        this.question_id = question_id;
    }

    public long getChoice_id() {
        return choice_id;
    }

    public void setChoice_id(long choice_id) {
        this.choice_id = choice_id;
    }

    public int getTime_answer() {
        return time_answer;
    }

    public void setTime_answer(int time_answer) {
        this.time_answer = time_answer;
    }

    public boolean isIs_right() {
        return is_right;
    }

    public void setIs_right(boolean is_right) {
        this.is_right = is_right;
    }
}
