package com.dev.geoquizworld;

import android.provider.BaseColumns;

public final class CountryReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private CountryReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_COUNTRIES = "Countries";

        public static final String COLUMN_COUNTRIES_COUNTRY_ID = "country_id";
        public static final String COLUMN_COUNTRIES_SHORTNAME = "shortName";
        public static final String COLUMN_COUNTRIES_NAME = "name";
        public static final String COLUMN_COUNTRIES_NATIVE = "native";
        public static final String COLUMN_COUNTRIES_CURRENCY = "currency";
        public static final String COLUMN_COUNTRIES_CONTINENT = "continent";
        public static final String COLUMN_COUNTRIES_CAPITAL = "capital";
        public static final String COLUMN_COUNTRIES_EMOJI = "emoji";
        public static final String COLUMN_COUNTRIES_EMOJIU = "emojiU";
        public static final String COLUMN_COUNTRIES_PHONE = "phone";
        public static final String COLUMN_COUNTRIES_EXTRACT = "extract";
        public static final String COLUMN_COUNTRIES_LATITUDE = "latitude";
        public static final String COLUMN_COUNTRIES_LONGITUDE = "longitude";
        public static final String COLUMN_COUNTRIES_REGION = "region";
        public static final String COLUMN_COUNTRIES_SUBREGION = "subregion";
        public static final String COLUMN_COUNTRIES_DEU = "deu";
        public static final String COLUMN_COUNTRIES_FRA = "fra";
        public static final String COLUMN_COUNTRIES_RUS = "rus";
        public static final String COLUMN_COUNTRIES_SPA = "spa";
        public static final String COLUMN_COUNTRIES_AREA = "area";
        public static final String COLUMN_COUNTRIES_INDEPENDENT = "independent";
        public static final String COLUMN_COUNTRIES_STATUS = "status";
        public static final String COLUMN_COUNTRIES_UNMEMBER = "unMember";
        public static final String COLUMN_COUNTRIES_USAGES = "usages";
        public static final String COLUMN_COUNTRIES_WON = "won";
        public static final String COLUMN_COUNTRIES_LOST = "lost";
        public static final String COLUMN_COUNTRIES_STREAK = "streak";
        public static final String COLUMN_LOCATION_USAGES = "loc_usages";
        public static final String COLUMN_LOCATION_WON = "loc_won";
        public static final String COLUMN_LOCATION_LOST = "loc_lost";
        public static final String COLUMN_LOCATION_STREAK = "loc_streak";
        public static final String COLUMN_COUNTRIES_SAVED = "saved";
    }

}
