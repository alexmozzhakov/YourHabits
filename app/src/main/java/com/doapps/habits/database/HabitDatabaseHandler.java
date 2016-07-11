package com.doapps.habits.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.doapps.habits.BuildConfig;
import com.doapps.habits.models.Habit;
import com.doapps.habits.models.HabitsDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressWarnings("HardCodedStringLiteral")
public class HabitDatabaseHandler extends SQLiteOpenHelper implements HabitsDatabase {

    private static final String TAG = HabitDatabaseHandler.class.getSimpleName();

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
    private static final String KEY_QUESTION = "question";
    private static final String KEY_UPDATED_DATE = "upd_d";
    private static final String KEY_UPDATED_MONTH = "upd_m";
    private static final String KEY_UPDATED_YEAR = "upd_y";
    private static final String KEY_TIME = "time";
    private static final String KEY_DONE = "done";
    private static final String KEY_FOLLOWING_FROM = "following_f";
    private static final String KEY_COST = "cost";
    private static final String KEY_FREQUENCY = "freq";
    private static boolean isSame;
    //private static int size;

    public HabitDatabaseHandler(final Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(final SQLiteDatabase sqLiteDatabase) {

        final StringBuilder createLoginTable = new StringBuilder("CREATE TABLE ")
                .append(TABLE_HABIT)
                .append('(')
                .append(KEY_ID).append(" INTEGER PRIMARY KEY,")
                .append(KEY_NAME).append(" TEXT,")
                .append(KEY_QUESTION).append(" TEXT,")
                .append(KEY_TIME).append(" INTEGER,")
                .append(KEY_UPDATED_DATE).append(" INTEGER,")
                .append(KEY_UPDATED_MONTH).append(" INTEGER,")
                .append(KEY_FOLLOWING_FROM).append(" INTEGER,")
                .append(KEY_COST).append(" INTEGER,")
                .append(KEY_FREQUENCY).append(" INTEGER,")
                .append(KEY_UPDATED_YEAR).append(" INTEGER,")
                .append(KEY_DONE).append(" INTEGER)");

        sqLiteDatabase.execSQL(createLoginTable.toString());

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(final SQLiteDatabase sqLiteDatabase, final int i, final int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_HABIT));

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    /**
     * Storing habit details in database
     */
    @Override
    public long addHabit(final String name, final String question, final int time,
                         final Calendar upd, final int followingFrom,
                         final int cost, final int frequency) {

        final SQLiteDatabase database = getWritableDatabase();
        //final int time,
        final ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Title
        values.put(KEY_QUESTION, question); // Question
        values.put(KEY_TIME, time); // Time
        values.put(KEY_UPDATED_DATE, upd.get(Calendar.DATE)); // Updated
        values.put(KEY_UPDATED_MONTH, upd.get(Calendar.MONTH)); // Updated
        values.put(KEY_UPDATED_YEAR, upd.get(Calendar.YEAR)); // Updated
        values.put(KEY_FOLLOWING_FROM, followingFrom); // date, from which followed
        values.put(KEY_COST, cost); // cost
        values.put(KEY_FREQUENCY, frequency); // frequency
        values.put(KEY_DONE, 0);

        // Inserting Row
        final long id = database.insert(TABLE_HABIT, null, values);
        database.close(); // Closing database connection

        isSame = false;
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("New habit inserted into sqlite: %d", id));
        }
        return id;
    }

    /**
     * Getting habit data from database
     */

    @Override
    public List<Habit> getHabitDetailsAsList() {

        final String selectQuery = String.format("SELECT  * FROM %s", TABLE_HABIT);

        final SQLiteDatabase database = getReadableDatabase();
        final Cursor cursor = database.rawQuery(selectQuery, null);
        final List<Habit> habitList = new ArrayList<>(cursor.getCount());

        // Move to first row
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            final Habit temp = new Habit(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)), // id
                    cursor.getString(cursor.getColumnIndex(KEY_NAME)), // title
                    cursor.getString(cursor.getColumnIndex(KEY_QUESTION)), // question
                    cursor.getInt(cursor.getColumnIndex(KEY_DONE)) == 1, // if activity is done
                    cursor.getInt(cursor.getColumnIndex(KEY_UPDATED_DATE)), // last date done
                    cursor.getInt(cursor.getColumnIndex(KEY_UPDATED_MONTH)), // last done month
                    cursor.getInt(cursor.getColumnIndex(KEY_UPDATED_YEAR)), // last done year
                    cursor.getInt(cursor.getColumnIndex(KEY_TIME)), // time, that a habit takes
                    cursor.getInt(cursor.getColumnIndex(KEY_FOLLOWING_FROM)), // date, from which followed
                    cursor.getInt(cursor.getColumnIndex(KEY_COST)), // cost
                    cursor.getInt(cursor.getColumnIndex(KEY_FREQUENCY)) // frequency
            );


            habitList.add(temp);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, temp.toString());
            }
            cursor.moveToNext();
        }

        cursor.close();
        database.close();
        isSame = true;

        return habitList;
    }

    @Override
    public void delete(final int position) {
        final SQLiteDatabase database = getWritableDatabase();
        database.execSQL(
                String.format("DELETE FROM %s WHERE %s = %d",
                        TABLE_HABIT, KEY_ID, position));

        if (BuildConfig.DEBUG) {
            Log.i(TAG,
                    String.format("DELETE FROM %s WHERE %s = %d",
                            TABLE_HABIT, KEY_ID, position));
        }
        database.close();
    }


    @Override
    public void updateHabit(final int id, final int day, final int month, final int year,
                            final int done) {

        final SQLiteDatabase database = getWritableDatabase();

        final String query = String.format("UPDATE %s SET %s = %d, %s = %d,%s = %d,%s = %d WHERE %s = %d",
                TABLE_HABIT, KEY_DONE, done,
                KEY_UPDATED_DATE, day, KEY_UPDATED_MONTH, month,
                KEY_UPDATED_YEAR, year, KEY_ID, id);
        database.execSQL(query);

        Log.i("SQL done", query);

        database.close();
        isSame = false;
    }

    @Override
    public void move(final int fromPosition, final int toPosition) {
        final SQLiteDatabase database = getWritableDatabase();

        database.execSQL(String.format("UPDATE %s SET %s = 0 WHERE %s = %d",
                TABLE_HABIT, KEY_ID, KEY_ID, fromPosition));
        // KEY_POSITION

        if (BuildConfig.DEBUG) {
            Log.i("SQL done", String.format("UPDATE %s SET %s = 0 WHERE %s = %d",
                    TABLE_HABIT, KEY_ID, KEY_ID, fromPosition));
        }

        database.execSQL(String.format("UPDATE %s SET %s = %d WHERE %s = %d",
                TABLE_HABIT, KEY_ID, fromPosition, KEY_ID, toPosition));

        if (BuildConfig.DEBUG) {
            Log.i("SQL done", String.format("UPDATE %s SET %s = %d WHERE %s = %d",
                    TABLE_HABIT, KEY_ID, fromPosition, KEY_ID, toPosition));
        }

        database.execSQL(String.format("UPDATE %s SET %s = %d WHERE %s = 0",
                TABLE_HABIT, KEY_ID, toPosition, KEY_ID));

        if (BuildConfig.DEBUG) {
            Log.i("SQL done", String.format("UPDATE %s SET %s = %d WHERE %s = 0",
                    TABLE_HABIT, KEY_ID, toPosition, KEY_ID));
        }

        database.close();
        isSame = false;
    }

    public static boolean isSame() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, String.format("Database changed: %s",
                    !isSame));
        }
        if (isSame) {
            return true;
        }
        isSame = true;
        return false;
    }

}
