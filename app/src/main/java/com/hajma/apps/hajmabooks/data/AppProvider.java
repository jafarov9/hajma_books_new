package com.hajma.apps.hajmabooks.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppProvider extends ContentProvider {

    SQLiteDatabase db;

    //Content Provider constans
    static final String CONTENT_AUTHORITY = "com.hajma.apps.hajmabooks.data.approvider";
    static final String PATH_USERS = "users";
    static final String PATH_BOOKS = "books";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final Uri CONTENT_URI_USERS = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_USERS);
    public static final Uri CONTENT_URI_BOOKS = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_BOOKS);

    static final UriMatcher matcher;

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(CONTENT_AUTHORITY, PATH_USERS, 1);
        matcher.addURI(CONTENT_AUTHORITY, PATH_BOOKS, 2);
    }

    //Database Constants
    private final static String DATABASE_NAME = "hajmabook.db";
    private final static int DATABASE_VERSION = 35;
    private final static String USERS_TABLE_NAME = "users";
    private final static String BOOKS_TABLE_NAME = "books";
    private final static String CREATE_USERS_TABLE = " CREATE TABLE "+ USERS_TABLE_NAME
            +" (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            +" name VARCHAR NOT NULL, "
            +" email VARCHAR NOT NULL, "
            +" username VARCHAR NOT NULL, "
            +" phone VARCHAR NOT NULL)";

    private final static String CREATE_BOOKS_TABLE = "CREATE TABLE "+ BOOKS_TABLE_NAME
            +" (bookId INTEGER, "
            +" path VARCHAR NOT NULL, "
            +" locator TEXT )";


    @Override
    public boolean onCreate() {
        DatabaseHelper helper = new DatabaseHelper(getContext());
        db = helper.getWritableDatabase();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor c = null;

        switch (matcher.match(uri)) {
            case 1:
                c = db.query(USERS_TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;

            case 2:
                //code book here
                c = db.query(BOOKS_TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
        }
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (matcher.match(uri)) {
            case 1:
                long columnIDUsersInsert = db.insert(USERS_TABLE_NAME, null, values);
                if(columnIDUsersInsert > 0) {
                    Uri _uri = ContentUris.withAppendedId(CONTENT_URI_USERS, columnIDUsersInsert);
                    return _uri;
                }
                break;

            case 2:
                //book code here
                long columnIDBooksInsert = db.insert(BOOKS_TABLE_NAME, null, values);
                if(columnIDBooksInsert > 0) {
                    Uri _uri = ContentUris.withAppendedId(CONTENT_URI_USERS, columnIDBooksInsert);
                    return _uri;
                }
                break;
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (matcher.match(uri)) {
            case 1:
                int columnUserDelete = db.delete(USERS_TABLE_NAME, selection, selectionArgs);
                return columnUserDelete;

            case 2:
                //book code here
        }
        return  0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (matcher.match(uri)) {
            case 1:
                int columnUserUpdate = db.update(USERS_TABLE_NAME, values, selection, selectionArgs);
                return columnUserUpdate;

            case 2:
                //book code here
                int columnBookUpdate = db.update(BOOKS_TABLE_NAME, values, selection, selectionArgs);
                return columnBookUpdate;
        }
        return  0;
    }


    //Database Helper class
    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_USERS_TABLE);
            db.execSQL(CREATE_BOOKS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+USERS_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS "+BOOKS_TABLE_NAME);
            onCreate(db);
        }
    }

}
