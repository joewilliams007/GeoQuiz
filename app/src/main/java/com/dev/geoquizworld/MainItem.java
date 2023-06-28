package com.dev.geoquizworld;

public class MainItem {
    String text;
    String type;
    Boolean correct;

    public MainItem(String type,String text, Boolean correct) {
        this.text = text;
        this.type = type;
        this.correct = correct;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public Boolean getCorrect() {
        return correct;
    }
}
