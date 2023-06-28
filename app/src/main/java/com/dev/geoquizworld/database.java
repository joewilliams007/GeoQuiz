package com.dev.geoquizworld;

public class database {
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CountryReaderContract.FeedEntry.TABLE_COUNTRIES + " (" +
                    CountryReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_NAME+ " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_CODE+ " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_EMOJI+ " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_UNICODE+ " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_USAGES+ " INTEGER," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_WON+ " INTEGER," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_LOST+ " INTEGER," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_STREAK+ " INTEGER," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_SAVED+ " BOOLEAN," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_IMAGE + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CountryReaderContract.FeedEntry.TABLE_COUNTRIES;

}
