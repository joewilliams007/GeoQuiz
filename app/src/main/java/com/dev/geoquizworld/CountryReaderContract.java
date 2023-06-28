package com.dev.geoquizworld;

import android.provider.BaseColumns;

public final class CountryReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private CountryReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_COUNTRIES = "Countries";
        public static final String COLUMN_COUNTRIES_NAME = "name";
        public static final String COLUMN_COUNTRIES_CODE = "code";
        public static final String COLUMN_COUNTRIES_EMOJI = "emoji";
        public static final String COLUMN_COUNTRIES_UNICODE = "unicode";
        public static final String COLUMN_COUNTRIES_IMAGE = "image";
        public static final String COLUMN_COUNTRIES_USAGES = "usages";
        public static final String COLUMN_COUNTRIES_WON = "won";
        public static final String COLUMN_COUNTRIES_LOST = "lost";
        public static final String COLUMN_COUNTRIES_STREAK = "streak";
        public static final String COLUMN_COUNTRIES_SAVED = "saved";
    }

}
