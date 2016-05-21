package com.habit_track.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class UserDBHandler extends SQLiteOpenHelper {

    private static final String TAG = UserDBHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "users_api";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CREATED_AT = "created_at";

    public UserDBHandler(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(final SQLiteDatabase database) {
        final String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_CREATED_AT + " TEXT" + ")";
        database.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(final SQLiteDatabase database, final int oldVersion, final int newVersion) {
        // Drop older table if existed
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(database);
    }

    /**
     * Storing user details in database
     */
    public void addUser(final String name, final String email, final String created_at) {
        final SQLiteDatabase database = this.getWritableDatabase();

        final ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        database.insert(TABLE_USER, null, values);
        database.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite");
    }

    /**
     * Getting user data from database
     */
    public Map<String, String> getUserDetails() {
        final HashMap<String, String> user = new HashMap<>();
        final String selectQuery = "SELECT  * FROM " + TABLE_USER;

        final SQLiteDatabase database = this.getReadableDatabase();
        final Cursor cursor = database.rawQuery(selectQuery, null);

        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("created_at", cursor.getString(3));
        }
        cursor.close();
        database.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteUsers() {
        final SQLiteDatabase database = this.getWritableDatabase();
        // Delete All Rows
        database.delete(TABLE_USER, null, null);
        database.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}
