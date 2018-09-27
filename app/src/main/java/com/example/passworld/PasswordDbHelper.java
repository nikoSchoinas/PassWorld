package com.example.passworld;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by New on 27/10/2017.
 */

public class PasswordDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Passwords.db";

    public PasswordDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_PASSWORD_TABLE =
                "CREATE TABLE " + PasswordContract.PasswordEntry.TABLE_NAME + " (" +
                        PasswordContract.PasswordEntry._ID + " INTEGER PRIMARY KEY," +
                        PasswordContract.PasswordEntry.COLUMN_NAME_SITE + " TEXT NOT NULL," +
                        PasswordContract.PasswordEntry.COLUMN_NAME_PASSWORD + " TEXT)";
        db.execSQL(SQL_CREATE_PASSWORD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
