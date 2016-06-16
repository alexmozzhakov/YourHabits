package com.habit_track.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.habit_track.models.Habit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HabitDBHandler extends SQLiteOpenHelper {

    private static final String TAG = HabitDBHandler.class.getSimpleName();

    private static HabitDBHandler dbHandler = null;

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

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
    private static final String KEY_FOLLOWING_FROM = "following_f";
    private static final String KEY_COST = "cost";
    private static final String KEY_FREQUENCY = "freq";
    public static boolean isChecked = false;

    public HabitDBHandler(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static HabitDBHandler getInstance(final Context context) {
        if (dbHandler == null) {
            dbHandler = new HabitDBHandler(context);
        }
        return dbHandler;
    }

    // Creating Tables
    @Override
    public void onCreate(final SQLiteDatabase database) {

        final String CREATE_LOGIN_TABLE =
                "CREATE TABLE " + TABLE_HABIT + "("
                        + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                        + KEY_DESCRIPTION + " TEXT," + KEY_TIME + " INTEGER,"
                        + KEY_UPDATED_DATE + " INTEGER," + KEY_UPDATED_MONTH + " INTEGER,"
                        + KEY_FOLLOWING_FROM + " INTEGER," + KEY_COST + " INTEGER,"
                        + KEY_FREQUENCY + " INTEGER," + KEY_UPDATED_YEAR + " INTEGER,"
                        + KEY_DONE + " INTEGER)";

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
     * Storing habit details in database
     */
    public long addHabit(final String name, final String description, final int time,
                         final boolean done, final Calendar upd, final int followingFrom,
                         final int cost, final int frequency) {

        final SQLiteDatabase database = this.getWritableDatabase();
        //final int time,
        final ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Title
        values.put(KEY_DESCRIPTION, description); // Description
        values.put(KEY_TIME, time); // Time
        values.put(KEY_UPDATED_DATE, upd.get(Calendar.DATE)); // Updated
        values.put(KEY_UPDATED_MONTH, upd.get(Calendar.MONTH)); // Updated
        values.put(KEY_UPDATED_YEAR, upd.get(Calendar.YEAR)); // Updated
        values.put(KEY_FOLLOWING_FROM, followingFrom); // date, from which followed
        values.put(KEY_COST, cost); // cost
        values.put(KEY_FREQUENCY, frequency); // frequency

        // doneMarker
        if (done) {
            values.put(KEY_DONE, 1);
        } else {
            values.put(KEY_DONE, 0);
        }

        // Inserting Row
        final long id = database.insert(TABLE_HABIT, null, values);
        database.close(); // Closing database connection

        isChecked = false;

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
            temp = new Habit(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)), // id
                    cursor.getString(cursor.getColumnIndex(KEY_NAME)), // title
                    cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)), // description
                    cursor.getInt(cursor.getColumnIndex(KEY_DONE)) == 1, // if activity is done
                    cursor.getInt(cursor.getColumnIndex(KEY_UPDATED_DATE)), // last date done
                    cursor.getInt(cursor.getColumnIndex(KEY_UPDATED_MONTH)), // last done month
                    cursor.getInt(cursor.getColumnIndex(KEY_UPDATED_YEAR)), // last done year
                    cursor.getInt(cursor.getColumnIndex(KEY_TIME)), // time, that a habit takes
                    cursor.getInt(cursor.getColumnIndex(KEY_FOLLOWING_FROM)), // date, from which followed
                    cursor.getInt(cursor.getColumnIndex(KEY_COST)), // cost
                    cursor.getInt(cursor.getColumnIndex(KEY_FREQUENCY)) // frequency
            );


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
        Log.i(TAG, "DELETE FROM " + TABLE_HABIT + " WHERE " + KEY_ID + " = " + id);
        database.close();
    }


    public void updateHabit(final int id, final int day, final int month, final int year,
                            final int done) {

        final SQLiteDatabase database = this.getWritableDatabase();

        final String query = "UPDATE " + TABLE_HABIT + " SET " + KEY_DONE + " = " + done + ", " +
                KEY_UPDATED_DATE + " = " + day + "," + KEY_UPDATED_MONTH + " = " + month + "," +
                KEY_UPDATED_YEAR + " = " + year + " WHERE " + KEY_ID + " = " + id;
        database.execSQL(query);

        Log.i("SQL done", query);
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
