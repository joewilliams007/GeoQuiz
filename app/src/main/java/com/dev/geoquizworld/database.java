package com.dev.geoquizworld;

public class database {
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CountryReaderContract.FeedEntry.TABLE_COUNTRIES + " (" +
                    CountryReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_COUNTRY_ID + " INTEGER," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_SHORTNAME + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_NAME + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_NATIVE + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_CURRENCY + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_CONTINENT + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_CAPITAL + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_EMOJI + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_EMOJIU + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_PHONE + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_EXTRACT + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_LATITUDE + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_LONGITUDE + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_REGION + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_SUBREGION + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_DEU + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_FRA + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_RUS + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_SPA + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_AREA + " INTEGER," +
                    CountryReaderContract.FeedEntry. COLUMN_COUNTRIES_INDEPENDENT + " BOOLEAN," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_STATUS + " TEXT," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_UNMEMBER + " BOOLEAN," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_USAGES+ " INTEGER," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_WON+ " INTEGER," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_LOST+ " INTEGER," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_STREAK+ " INTEGER," +
                    CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_SAVED+ " BOOLEAN)";


    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CountryReaderContract.FeedEntry.TABLE_COUNTRIES;

}
