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
    private static final int DATABASE_VERSION = 1;

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
    private static final String KEY_FREQUENCY_ARRAY = "freq_arr";
    private static final String INTEGER = " INTEGER,";
    private static boolean isSame = true;

    public HabitDatabaseHandler(final Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static short[] stringToShortArray(final String str) {
        final String[] temp = str.split(",");
        final short[] shorts = new short[temp.length];
        for (int i = 0; i < shorts.length && i < temp.length; i++) {
            shorts[i] = Short.valueOf(temp[i]);
        }
        return shorts;
    }

    @Override
    public boolean isEmpty() {
        final SQLiteDatabase database = getWritableDatabase();
        final Cursor cursor = database.rawQuery(
                String.format("SELECT * FROM %s", TABLE_HABIT), null);
        final boolean isEmpty = cursor.getCount() == 0;
        cursor.close();
        database.close();
        return isEmpty;
    }

    private static String integerArrayToString(final int... ints) {
        final StringBuilder sb = new StringBuilder(10);
        for (final int i : ints) {
            sb.append(i).append(',');
        }
        return sb.toString();
    }

    // Creating Tables
    @Override
    public void onCreate(final SQLiteDatabase sqLiteDatabase) {

        final StringBuilder createLoginTable = new StringBuilder(106)
                .append("CREATE TABLE ")
                .append(TABLE_HABIT)
                .append('(')
                .append(KEY_ID).append(" INTEGER PRIMARY KEY,")
                .append(KEY_NAME).append(" TEXT,")
                .append(KEY_QUESTION).append(" TEXT,")
                .append(KEY_TIME).append(INTEGER)
                .append(KEY_UPDATED_DATE).append(INTEGER)
                .append(KEY_UPDATED_MONTH).append(INTEGER)
                .append(KEY_FOLLOWING_FROM).append(INTEGER)
                .append(KEY_COST).append(INTEGER)
                .append(KEY_FREQUENCY_ARRAY).append(" TEXT,")
                .append(KEY_UPDATED_YEAR).append(INTEGER)
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
    public void addHabit(final String name, final String question, final int time,
                         final Calendar upd, final int cost, final int... frequency) {

        final SQLiteDatabase database = getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Title
        values.put(KEY_QUESTION, question); // Question
        values.put(KEY_TIME, time); // Time
        values.put(KEY_UPDATED_DATE, upd.get(Calendar.DATE)); // Updated
        values.put(KEY_UPDATED_MONTH, upd.get(Calendar.MONTH)); // Updated
        values.put(KEY_UPDATED_YEAR, upd.get(Calendar.YEAR)); // Updated
        values.put(KEY_FOLLOWING_FROM, System.currentTimeMillis()); // Day last followed
        values.put(KEY_COST, cost); // cost
        values.put(KEY_FREQUENCY_ARRAY, integerArrayToString(frequency)); // frequency
        values.put(KEY_DONE, 0);

        // Inserting Row
        final long id = database.insert(TABLE_HABIT, null, values);
        database.close(); // Closing database connection

        isSame = false;
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("New habit inserted into sqlite: %d", id));
        }
    }


    @Override
    public long addHabit(final String name, final String question, final int time,
                         final Calendar upd, final int cost, final String frequency) {

        final SQLiteDatabase database = getWritableDatabase();
        //final int time,
        final ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Title
        values.put(KEY_QUESTION, question); // Question
        values.put(KEY_TIME, time); // Time
        values.put(KEY_UPDATED_DATE, upd.get(Calendar.DATE)); // Updated
        values.put(KEY_UPDATED_MONTH, upd.get(Calendar.MONTH)); // Updated
        values.put(KEY_UPDATED_YEAR, upd.get(Calendar.YEAR)); // Updated
        values.put(KEY_FOLLOWING_FROM, System.currentTimeMillis()); // Day last followed
        values.put(KEY_COST, cost); // cost
        values.put(KEY_FREQUENCY_ARRAY, frequency); // frequency
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
        Log.w("Database", "getHabitDetailsAsList()");

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
                    cursor.getInt(cursor.getColumnIndex(KEY_UPDATED_DATE)), // last markerUpdatedDate done
                    cursor.getInt(cursor.getColumnIndex(KEY_UPDATED_MONTH)), // last done month
                    cursor.getInt(cursor.getColumnIndex(KEY_UPDATED_YEAR)), // last done year
                    cursor.getInt(cursor.getColumnIndex(KEY_TIME)), // time, that a habit takes
                    cursor.getInt(cursor.getColumnIndex(KEY_FOLLOWING_FROM)), // Day last followed
                    cursor.getInt(cursor.getColumnIndex(KEY_COST)), // cost
                    stringToShortArray(
                            cursor.getString(cursor.getColumnIndex(KEY_FREQUENCY_ARRAY)))// frequency
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
                String.format("DELETE FROM %s WHERE %s = %d", TABLE_HABIT, KEY_ID, position));

        if (BuildConfig.DEBUG) {
            Log.i(TAG, String.format("DELETE FROM %s WHERE %s = %d", TABLE_HABIT, KEY_ID, position));
        }
        database.close();
    }

    @Override
    public void updateHabit(final Habit habit, final int done) {
        final SQLiteDatabase database = getWritableDatabase();

        final String query = String.format("UPDATE %s SET %s = %d, %s = %d,%s = %d,%s = %d WHERE %s = %d",
                TABLE_HABIT, KEY_DONE, done,
                KEY_UPDATED_DATE, habit.getMarkerUpdatedDay(), KEY_UPDATED_MONTH, habit.getMarkerUpdatedMonth(),
                KEY_UPDATED_YEAR, habit.getMarkerUpdatedYear(), KEY_ID, habit.id);
        database.execSQL(query);
        Log.i(TAG, query);

        if (done == 1 && mustHaveFollowed(habit)) {
            final String setFollowingCounter = String.format("UPDATE %s SET %s = %d WHERE %s = %d",
                    TABLE_HABIT, KEY_FOLLOWING_FROM, System.currentTimeMillis(), KEY_ID, habit.id);
            database.execSQL(setFollowingCounter);
            Log.i(TAG, setFollowingCounter);
        }

        database.close();
        isSame = false;
    }

    @Override
    public void move(final int fromPosition, final int toPosition) {
        final SQLiteDatabase database = getWritableDatabase();

        database.execSQL(String.format("UPDATE %s SET %s = 0 WHERE %s = %d",
                TABLE_HABIT, KEY_ID, KEY_ID, fromPosition));

        if (BuildConfig.DEBUG) {
            Log.i(TAG, String.format("UPDATE %s SET %s = 0 WHERE %s = %d",
                    TABLE_HABIT, KEY_ID, KEY_ID, fromPosition));
        }

        database.execSQL(String.format("UPDATE %s SET %s = %d WHERE %s = %d",
                TABLE_HABIT, KEY_ID, fromPosition, KEY_ID, toPosition));

        if (BuildConfig.DEBUG) {
            Log.i(TAG, String.format("UPDATE %s SET %s = %d WHERE %s = %d",
                    TABLE_HABIT, KEY_ID, fromPosition, KEY_ID, toPosition));
        }

        database.execSQL(String.format("UPDATE %s SET %s = %d WHERE %s = 0",
                TABLE_HABIT, KEY_ID, toPosition, KEY_ID));

        if (BuildConfig.DEBUG) {
            Log.i(TAG, String.format("UPDATE %s SET %s = %d WHERE %s = 0",
                    TABLE_HABIT, KEY_ID, toPosition, KEY_ID));
        }

        database.close();
    }

    public static boolean notSame() {
        return !isSame;
    }

    private static int daysTillNow(final Habit habit) {
        android.os.Debug.startMethodTracing("daysTillNow");
        final Calendar updateDate = Calendar.getInstance();
        updateDate.set(habit.getMarkerUpdatedYear(), habit.getMarkerUpdatedMonth(), habit.getMarkerUpdatedDay());

        final long millis1 = System.currentTimeMillis();
        final long millis2 = updateDate.getTimeInMillis();
        final long diff = millis2 - millis1;
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Not marked for " + diff / 86400000);
        }
        android.os.Debug.stopMethodTracing();
        return (int) (diff / 86400000);

    }

    private static boolean mustHaveFollowed(final Habit habit) {
        final int daysTillNow = daysTillNow(habit);
        final short[] freq = habit.getFrequencyArray();
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Must have followed = " + (daysTillNow > freq[freq.length - 1]));
        }
        return daysTillNow > freq[freq.length - 1]
                || daysTillNow == 1 && freq[freq.length - 1] == 0;
    }
}
