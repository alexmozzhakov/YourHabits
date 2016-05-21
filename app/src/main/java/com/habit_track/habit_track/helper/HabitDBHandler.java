package com.habit_track.habit_track.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.habit_track.habit_track.app.Habit;

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

    // Habit table name
    private static final String TABLE_HABIT = "habit";

    // Habit Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_UPDATED_DATE = "upd_d";
    private static final String KEY_UPDATED_MONTH = "upd_m";
    private static final String KEY_UPDATED_YEAR = "upd_y";
    private static final String KEY_TIME = "time";
    private static final String KEY_DONE = "done";
    public static boolean isChecked = false;

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
    public void onUpgrade(final SQLiteDatabase database, final int oldVersion, final int newVersion) {
        // Drop older table if existed
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_HABIT);

        // Create tables again
        onCreate(database);
    }

    /**
     * Storing user details in database
     */
    public long addHabit(final String name, final String description, final int time,
                         final boolean done, final Calendar upd) {

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
        } else {
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
            temp = new Habit(cursor.getString(cursor.getColumnIndex(KEY_NAME)), i);
            temp.doneMarker = cursor.getInt(cursor.getColumnIndex(KEY_DONE)) == 1;
            temp.id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            temp.markerUpdatedDay = cursor.getInt(cursor.getColumnIndex(KEY_UPDATED_DATE));
            temp.markerUpdatedMonth = cursor.getInt(cursor.getColumnIndex(KEY_UPDATED_MONTH));
            temp.markerUpdatedYear = cursor.getInt(cursor.getColumnIndex(KEY_UPDATED_YEAR));
            temp.time = cursor.getInt(cursor.getColumnIndex(KEY_TIME));

            hab.add(temp);
            Log.d(TAG, temp.toString());
            cursor.moveToNext();
        }

        cursor.close();
        database.close();
        isChecked = true;

        return hab;
    }

    public void delete(final int id) {
        final SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_HABIT + " WHERE " + KEY_ID + " = " + id);
        Log.i("SQL done", "DELETE FROM " + TABLE_HABIT + " WHERE " + KEY_ID + " = " + id);
        database.close();
    }


    public void updateHabit(final int id, final int done,
                            final int day, final int month, final int year) {

        final SQLiteDatabase database = this.getWritableDatabase();

        database.execSQL("UPDATE " + TABLE_HABIT + " SET " + KEY_DONE + " = " + done + ", " + KEY_UPDATED_DATE + " = " + day + "," + KEY_UPDATED_MONTH + " = " + month + "," + KEY_UPDATED_YEAR + " = " + year + " WHERE " + KEY_ID + " = " + id);
        Log.i("SQL done", "UPDATE " + TABLE_HABIT + " SET " + KEY_DONE + " = " + done + ", " + KEY_UPDATED_DATE + " = " + day + "," + KEY_UPDATED_MONTH + " = " + month + "," + KEY_UPDATED_YEAR + " = " + year + " WHERE " + KEY_ID + " = " + id);
    }

    public void move(int fromPosition, int toPosition) {
        final SQLiteDatabase database = this.getWritableDatabase();
        fromPosition++;
        toPosition++;

        database.execSQL("UPDATE " + TABLE_HABIT +
                " SET " + KEY_ID + " = -" + fromPosition +
                " WHERE " + KEY_ID + " = " + fromPosition);

        Log.i("SQL done", "UPDATE " + TABLE_HABIT +
                " SET " + KEY_ID + " = -" + fromPosition +
                " WHERE " + KEY_ID + " = " + fromPosition);

        database.execSQL("UPDATE " + TABLE_HABIT +
                " SET " + KEY_ID + " = " + fromPosition +
                " WHERE " + KEY_ID + " = " + toPosition);

        Log.i("SQL done", "UPDATE " + TABLE_HABIT +
                " SET " + KEY_ID + " = " + fromPosition +
                " WHERE " + KEY_ID + " = " + toPosition);

        database.execSQL("UPDATE " + TABLE_HABIT +
                " SET " + KEY_ID + " = " + toPosition +
                " WHERE " + KEY_ID + " = -" + fromPosition);

        Log.i("SQL done", "UPDATE " + TABLE_HABIT +
                " SET " + KEY_ID + " = " + toPosition +
                " WHERE " + KEY_ID + " = -" + fromPosition);

        isChecked = false;
    }

    public boolean notSame() {
        Log.i(TAG, "Database changed: " + !isChecked);
        if (isChecked) {
            return false;
        }
        isChecked = true;
        return true;
    }

}
