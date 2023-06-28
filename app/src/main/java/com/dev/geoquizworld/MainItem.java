package com.dev.geoquizworld;

public class MainItem {
    String text;
    String type;
    Boolean correct;
    String emoji;

    public MainItem(String type,String text, Boolean correct, String emoji) {
        this.text = text;
        this.type = type;
        this.correct = correct;
        this.emoji = emoji;
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

    public String getEmoji() {
        return emoji;
    }
}
