package com.dev.geoquizworld;

public class Country {
    Long _id;

    Integer country_id;
    String shortName, name, nativeName, currency, continent, capital, emoji, emojiU, phone, extract, latitude, longitude, region, subregion, deu, fra, rus, spa;
    Integer area;
    Boolean independent;
    String status;
    Boolean unMember;
    Integer usages, won, lost, streak;
    Boolean saved;

    public Country(Long _id, Integer country_id, String shortName, String name, String nativeName, String currency, String continent, String capital, String emoji, String emojiU, String phone, String extract, String latitude, String longitude, String region, String subregion, String deu, String fra, String rus, String spa, Integer area, Boolean independent, String status, Boolean unMember, Integer usages, Integer won, Integer lost, Integer streak, Boolean saved) {
        this._id = _id;
        this.country_id = country_id;
        this.shortName = shortName;
        this.name = name;
        this.nativeName = nativeName;
        this.currency = currency;
        this.continent = continent;
        this.capital = capital;
        this.emoji = emoji;
        this.emojiU = emojiU;
        this.phone = phone;
        this.extract = extract;
        this.latitude = latitude;
        this.longitude = longitude;
        this.region = region;
        this.subregion = subregion;
        this.deu = deu;
        this.fra = fra;
        this.rus = rus;
        this.spa = spa;
        this.area = area;
        this.independent = independent;
        this.status = status;
        this.unMember = unMember;
        this.usages = usages;
        this.won = won;
        this.lost = lost;
        this.streak = streak;
        this.saved = saved;
    }

    public Long get_id() {
        return _id;
    }

    public Integer getCountry_id() {
        return country_id;
    }

    public String getShortName() {
        return shortName;
    }

    public String getName() {
        return name;
    }

    public String getNativeName() {
        return nativeName;
    }

    public String getCurrency() {
        return currency;
    }

    public String getContinent() {
        return continent;
    }

    public String getCapital() {
        return capital;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getEmojiU() {
        return emojiU;
    }

    public String getPhone() {
        return phone;
    }

    public String getExtract() {
        return extract;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getRegion() {
        return region;
    }

    public String getSubregion() {
        return subregion;
    }

    public String getDeu() {
        return deu;
    }

    public String getFra() {
        return fra;
    }

    public String getRus() {
        return rus;
    }

    public String getSpa() {
        return spa;
    }

    public Integer getArea() {
        return area;
    }

    public Boolean getIndependent() {
        return independent;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getUnMember() {
        return unMember;
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
