package com.dev.geoquizworld;

public class Country {
    Long _id;
    String name;
    String code;
    String emoji;
    String unicode;
    String image;
    Integer usages;
    Integer won;
    Integer lost;
    Integer streak;
    Boolean saved;


    public Country(Long _id, String name, String code, String emoji, String unicode, String image, Integer usages, Integer won, Integer lost, Integer streak, Boolean saved) {
        this._id = _id;
        this.name = name;
        this.code = code;
        this.emoji = emoji;
        this.unicode = unicode;
        this.image = image;
        this.usages = usages;
        this.won = won;
        this.lost = lost;
        this.streak = streak;
        this.saved = saved;
    }

    public Long get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getUnicode() {
        return unicode;
    }

    public String getImage() {
        return image;
    }

    public Integer getUsages() {
        return usages;
    }

    public Integer getWon() {
        return won;
    }

    public Integer getLost() {
        return lost;
    }

    public Integer getStreak() {
        return streak;
    }

    public Boolean getSaved() {
        return saved;
    }

}
