package com.example.passworld;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by New on 27/10/2017.
 */

public final class PasswordContract {
    private PasswordContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.passworld";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PASSWORDS = "passwords";



    /* Inner class that defines the table contents */
    public static final class PasswordEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PASSWORDS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PASSWORDS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PASSWORDS;



        public static final String TABLE_NAME = "passwords";
        public final static String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_SITE = "site";
        public static final String COLUMN_NAME_PASSWORD = "password";
    }

}
