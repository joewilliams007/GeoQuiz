package com.dev.geoquizworld;

import static com.dev.geoquizworld.CountryReaderContract.FeedEntry.COLUMN_LOCATION_LOST;
import static com.dev.geoquizworld.CountryReaderContract.FeedEntry.COLUMN_LOCATION_STREAK;
import static com.dev.geoquizworld.CountryReaderContract.FeedEntry.COLUMN_LOCATION_USAGES;
import static com.dev.geoquizworld.CountryReaderContract.FeedEntry.COLUMN_LOCATION_WON;
import static com.dev.geoquizworld.CountryReaderContract.FeedEntry.TABLE_COUNTRIES;
import static com.dev.geoquizworld.database.SQL_CREATE_ENTRIES;
import static com.dev.geoquizworld.database.SQL_DELETE_ENTRIES;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CountryReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "FeedReader.db";
    private static final String DATABASE_ALTER_COLUMN_LOCATION_USAGES = "ALTER TABLE "
            + TABLE_COUNTRIES + " ADD COLUMN " + COLUMN_LOCATION_USAGES + " INTEGER DEFAULT 0;";
    private static final String DATABASE_ALTER_COLUMN_LOCATION_WON = "ALTER TABLE "
            + TABLE_COUNTRIES + " ADD COLUMN " + COLUMN_LOCATION_WON + " INTEGER DEFAULT 0;";
    private static final String DATABASE_ALTER_COLUMN_LOCATION_LOST = "ALTER TABLE "
            + TABLE_COUNTRIES + " ADD COLUMN " + COLUMN_LOCATION_LOST + " INTEGER DEFAULT 0;";
    private static final String DATABASE_ALTER_COLUMN_LOCATION_STREAK = "ALTER TABLE "
            + TABLE_COUNTRIES + " ADD COLUMN " + COLUMN_LOCATION_STREAK + " INTEGER DEFAULT 0;";
    public CountryReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is to simply to discard the data and start over
        //db.execSQL(SQL_DELETE_ENTRIES);
        //onCreate(db);

        if (oldVersion < 3) {
            db.execSQL(DATABASE_ALTER_COLUMN_LOCATION_USAGES);
            db.execSQL(DATABASE_ALTER_COLUMN_LOCATION_WON);
            db.execSQL(DATABASE_ALTER_COLUMN_LOCATION_LOST);
            db.execSQL(DATABASE_ALTER_COLUMN_LOCATION_STREAK);
        }
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

