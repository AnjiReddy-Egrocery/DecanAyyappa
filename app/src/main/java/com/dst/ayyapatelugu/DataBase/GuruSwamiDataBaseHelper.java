package com.dst.ayyapatelugu.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class GuruSwamiDataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "guru_swami_database";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "guru_swami_table";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_SMALL_DESCRIPTION = "small_description";
    public static final String COLUMN_IMAGE_URL = "image_url";

    public GuruSwamiDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_SMALL_DESCRIPTION + " TEXT, " +
                COLUMN_IMAGE_URL + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }
}
