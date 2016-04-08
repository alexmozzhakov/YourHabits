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
import java.util.List;

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

    public HabitDBHandler(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(final SQLiteDatabase database) {
        final String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_HABIT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_DESCRIPTION + " TEXT," + KEY_TIME + " INTEGER,"
                + KEY_UPDATED_DATE + " INTEGER," + KEY_UPDATED_MONTH + " INTEGER,"
                + KEY_UPDATED_YEAR + " INTEGER," + KEY_DONE + " INTEGER" + ")";
        database.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(final SQLiteDatabase database,final int oldVersion,final int newVersion) {
        // Drop older table if existed
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_HABIT);

        // Create tables again
        onCreate(database);
    }

    /**
     * Storing user details in database
     */
    public long addHabit(final String name, final String description,final int time, final boolean done, final Calendar upd) {
        final SQLiteDatabase database = this.getWritableDatabase();
        //final int time,
        final ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Title
        values.put(KEY_DESCRIPTION, description); // Description
        values.put(KEY_TIME, time); // Time
        values.put(KEY_UPDATED_DATE, upd.get(Calendar.DATE)); // Updated
        values.put(KEY_UPDATED_MONTH, upd.get(Calendar.MONTH)); // Updated
        values.put(KEY_UPDATED_YEAR, upd.get(Calendar.YEAR)); // Updated

        // doneMarker
        if (done) {
            values.put(KEY_DONE, 1);
        }
        else {
            values.put(KEY_DONE, 0);
        }

        // Inserting Row
        final long id = database.insert(TABLE_HABIT, null, values);
        database.close(); // Closing database connection

        Log.d(TAG, "New habit inserted into sqlite: " + id);
        return id;
    }

    /**
     * Getting habit data from database
     */

    public List<Habit> getHabitDetailsAsArrayList() {

        final String selectQuery = "SELECT  * FROM " + TABLE_HABIT;

        final SQLiteDatabase database = this.getReadableDatabase();
        final Cursor cursor = database.rawQuery(selectQuery, null);
        final List<Habit> hab = new ArrayList<>();

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
            temp.time = cursor.getInt(cursor.getColumnIndex(KEY_TIME));

            hab.add(temp);
            Log.d(TAG, hab.get(i).toString());
            cursor.moveToNext();
        }

        cursor.close();
        database.close();
        // return habit
        //  Log.d(TAG, "Fetching habits from Sqlite: " + Arrays.toString(habits));

        return hab;
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteHabits() {
        final SQLiteDatabase database = this.getWritableDatabase();
        // Delete All Rows
        database.delete(TABLE_HABIT, null, null);
        database.close();

        Log.d(TAG, "Deleted all habits from sqlite");
    }

    public void deleteHabit(final int id) {
        final SQLiteDatabase database = this.getWritableDatabase();
        // Delete All Rows
        database.execSQL("DELETE FROM " + TABLE_HABIT + " WHERE " + KEY_ID + " = " + id);
        Log.i("SQL done", "DELETE FROM " + TABLE_HABIT + " WHERE " + KEY_ID + " = " + id);
        //db.delete(TABLE_HABIT, null, null);
        database.close();

        // Log.d(TAG, "Deleted habit with id" + id);
    }

    public void updateHabit(int id, int day, int month, int year) {
        final SQLiteDatabase database = this.getWritableDatabase();

        database.execSQL("UPDATE " + TABLE_HABIT + " SET " + KEY_DONE + " =  1, " + KEY_UPDATED_DATE + " = " + day + "," + KEY_UPDATED_MONTH + " = " + month + "," + KEY_UPDATED_YEAR + " = " + year + " WHERE " + KEY_ID + " = " + id);
        Log.i("SQL done", "UPDATE " + TABLE_HABIT + " SET " + KEY_DONE + " =  1, " + KEY_UPDATED_DATE + " = " + day + "," + KEY_UPDATED_MONTH + " = " + month + "," + KEY_UPDATED_YEAR + " = " + year + " WHERE " + KEY_ID + " = " + id);
    }
}
