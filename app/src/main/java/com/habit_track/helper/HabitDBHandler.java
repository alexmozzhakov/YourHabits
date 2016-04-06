package com.habit_track.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.habit_track.app.Habit;

import java.util.ArrayList;
import java.util.Calendar;

public class HabitDBHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "habits";

    // Login table name
    private static final String TABLE_HABIT = "habit";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_UPDATED_DATE = "upd_d";
    private static final String KEY_UPDATED_MONTH = "upd_m";
    private static final String KEY_UPDATED_YEAR = "upd_y";
    private static final String KEY_TIME = "time";
    private static final String KEY_DONE = "done";

    public HabitDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_HABIT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_DESCRIPTION + " TEXT," + KEY_TIME + " INTEGER,"
                + KEY_UPDATED_DATE + " INTEGER," + KEY_UPDATED_MONTH + " INTEGER,"
                + KEY_UPDATED_YEAR + " INTEGER," + KEY_DONE + " INTEGER" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABIT);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public long addHabit(String name, String description, int time, boolean done, Calendar upd) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Title
        values.put(KEY_DESCRIPTION, description); // Description
        values.put(KEY_TIME, time); // Time
        values.put(KEY_UPDATED_DATE, upd.get(Calendar.DATE)); // Updated
        values.put(KEY_UPDATED_MONTH, upd.get(Calendar.MONTH)); // Updated
        values.put(KEY_UPDATED_YEAR, upd.get(Calendar.YEAR)); // Updated

        // doneMarker
        if (done) values.put(KEY_DONE, 1);
        else values.put(KEY_DONE, 0);

        // Inserting Row
        long id = db.insert(TABLE_HABIT, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New habit inserted into sqlite: " + id);
        return id;
    }

    /**
     * Getting habit data from database
     */

    public ArrayList<Habit> getHabitDetailsAsArrayList() {

        String selectQuery = "SELECT  * FROM " + TABLE_HABIT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Habit> hab = new ArrayList<>();

        // Move to first row
        cursor.moveToFirst();
        Habit temp;
        for (int i = 0; i < cursor.getCount(); i++) {
            temp = new Habit(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            temp.doneMarker = cursor.getInt(cursor.getColumnIndex(KEY_DONE)) == 1;
            temp.id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            temp.markerUpdatedDay = cursor.getInt(cursor.getColumnIndex(KEY_UPDATED_DATE));
            temp.markerUpdatedMonth = cursor.getInt(cursor.getColumnIndex(KEY_UPDATED_MONTH));
            temp.markerUpdatedYear = cursor.getInt(cursor.getColumnIndex(KEY_UPDATED_YEAR));

            hab.add(temp);
            Log.d(TAG, hab.get(i).toString());
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        // return habit
        //  Log.d(TAG, "Fetching habits from Sqlite: " + Arrays.toString(habits));

        return hab;
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteHabits() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_HABIT, null, null);
        db.close();

        Log.d(TAG, "Deleted all habits from sqlite");
    }

    public void deleteHabit(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.execSQL("DELETE FROM " + TABLE_HABIT + " WHERE " + KEY_ID + " = " + id);
        Log.i("SQL done", "DELETE FROM " + TABLE_HABIT + " WHERE " + KEY_ID + " = " + id);
        //db.delete(TABLE_HABIT, null, null);
        db.close();

        // Log.d(TAG, "Deleted habit with id" + id);
    }

    public void updateHabit(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE " + TABLE_HABIT + " SET " + KEY_DONE + " =  1 WHERE " + KEY_ID + " = " + id);
        Log.i("SQL done", "UPDATE " + TABLE_HABIT + " SET " + KEY_DONE + " = 1 WHERE " + KEY_ID + " = " + id);
    }

    public void updateHabit(int id, int day, int month, int year) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE " + TABLE_HABIT + " SET " + KEY_DONE + " =  1, " + KEY_UPDATED_DATE + "," + KEY_UPDATED_MONTH + " = " + month + "," + KEY_UPDATED_YEAR + " = " + year + " WHERE " + KEY_ID + " = " + id);
        Log.i("SQL done", "UPDATE " + TABLE_HABIT + " SET " + KEY_DONE + " =  1, " + KEY_UPDATED_DATE + "," + KEY_UPDATED_MONTH + " = " + month + "," + KEY_UPDATED_YEAR + " = " + year + " WHERE " + KEY_ID + " = " + id);
    }
}
