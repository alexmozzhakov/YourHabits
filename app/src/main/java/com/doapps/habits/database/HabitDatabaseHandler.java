package com.doapps.habits.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.doapps.habits.BuildConfig;
import com.doapps.habits.models.Habit;
import com.doapps.habits.models.IHabitsDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressLint("DefaultLocale")
public class HabitDatabaseHandler extends SQLiteOpenHelper implements IHabitsDatabase {
    /**
     * TAG is defined for logging errors and debugging information
     */
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
    private static final String TEXT = " TEXT,";
    /**
     * isSame is for managing table status, not to load every time from database, but use previously
     * generated list in {@link:HabitListManager}
     */
    private static boolean isSame = true;

    public HabitDatabaseHandler(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static int[] stringToIntArray(String str) {
        String[] temp = str.split(",");
        int[] arr = new int[temp.length];
        for (int i = 0; i < arr.length && i < temp.length; i++) {
            arr[i] = Integer.valueOf(temp[i]);
        }

        return arr;
    }

    @Override
    public boolean isEmpty() {
        String quString = "select exists(select 1 from " + TABLE_HABIT + ");";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(quString, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        boolean flag = count != 1;
        cursor.close();
        db.close();

        return flag;
    }

    private static String integerArrayToString(int... ints) {
        StringBuilder sb = new StringBuilder(ints.length);
        for (int i : ints) {
            sb.append(i).append(',');
        }
        return sb.toString();
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createLoginTable = "CREATE TABLE " +
                TABLE_HABIT + '(' +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NAME + TEXT +
                KEY_QUESTION + TEXT +
                KEY_TIME + INTEGER +
                KEY_UPDATED_DATE + INTEGER +
                KEY_UPDATED_MONTH + INTEGER +
                KEY_UPDATED_YEAR + INTEGER +
                KEY_FOLLOWING_FROM + INTEGER +
                KEY_COST + INTEGER +
                KEY_FREQUENCY_ARRAY + TEXT +
                KEY_DONE + " INTEGER)";

        sqLiteDatabase.execSQL(createLoginTable);
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_HABIT));
        // Create tables again
        onCreate(sqLiteDatabase);
    }

    /**
     * Storing habit details in database
     */
    @Override
    public long addHabit(String name, String question, int time,
                         Calendar upd, int cost, int... frequency) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Title
        values.put(KEY_QUESTION, question); // Question
        values.put(KEY_TIME, time); // Time
        values.put(KEY_UPDATED_DATE, upd.get(Calendar.DATE)); // Updated
        values.put(KEY_UPDATED_MONTH, upd.get(Calendar.MONTH)); // Updated
        values.put(KEY_UPDATED_YEAR, upd.get(Calendar.YEAR)); // Updated
        values.put(KEY_FOLLOWING_FROM, System.currentTimeMillis()); // Day last followed
        values.put(KEY_COST, cost); // cost
        values.put(KEY_FREQUENCY_ARRAY, integerArrayToString(frequency)); // frequency
        // Inserting Row
        long id = database.insert(TABLE_HABIT, null, values);
        // Closing database connection
        database.close();
        isSame = false;
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("New habit inserted into sqlite: %d", id));
        }

        return id;
    }


    @Override
    public long addHabit(String name, String question, int time,
                         Calendar upd, int cost, String frequency) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Title
        values.put(KEY_QUESTION, question); // Question
        values.put(KEY_TIME, time); // Time
        values.put(KEY_UPDATED_DATE, upd.get(Calendar.DATE)); // Updated
        values.put(KEY_UPDATED_MONTH, upd.get(Calendar.MONTH)); // Updated
        values.put(KEY_UPDATED_YEAR, upd.get(Calendar.YEAR)); // Updated
        values.put(KEY_FOLLOWING_FROM, System.currentTimeMillis()); // Day last followed
        values.put(KEY_COST, cost); // cost
        values.put(KEY_FREQUENCY_ARRAY, frequency); // frequency
        // Inserting Row
        long id = database.insert(TABLE_HABIT, null, values);
        // Closing database connection
        database.close();
        isSame = false;
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("New habit inserted into sqlite: %d", id));
        }
        return id;
    }

    /**
     * Getting habit data from database
     */

    private static Habit cursorToHabit(Cursor cursor) {
        return new Habit(
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
                stringToIntArray(
                        cursor.getString(cursor.getColumnIndex(KEY_FREQUENCY_ARRAY)))// frequency
        );
    }

    @Override
    public List<Habit> getHabitDetailsAsList() {
        Log.w("Database", "getHabitDetailsAsList()");

        String selectQuery = String.format("SELECT  * FROM %s", TABLE_HABIT);

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<Habit> habitList = new ArrayList<>(cursor.getCount());

        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            Habit temp = cursorToHabit(cursor);

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
    public void delete(int position) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(
                String.format("DELETE FROM %s WHERE %s = %d", TABLE_HABIT, KEY_ID, position));

        if (BuildConfig.DEBUG) {
            Log.i(TAG, String.format("DELETE FROM %s WHERE %s = %d", TABLE_HABIT, KEY_ID, position));
        }
        database.close();
    }

    @Override
    public void updateHabit(Habit habit, int done) {
        SQLiteDatabase database = getWritableDatabase();

        String query = String.format("UPDATE %s SET %s = %d, %s = %d,%s = %d,%s = %d WHERE %s = %d",
                TABLE_HABIT, KEY_DONE, done,
                KEY_UPDATED_DATE, habit.getMarkerUpdatedDay(), KEY_UPDATED_MONTH, habit.getMarkerUpdatedMonth(),
                KEY_UPDATED_YEAR, habit.getMarkerUpdatedYear(), KEY_ID, habit.id);
        database.execSQL(query);
        Log.i(TAG, query);

        if (done == 1 && mustHaveFollowed(habit)) {
            String setFollowingCounter = String.format("UPDATE %s SET %s = %d WHERE %s = %d",
                    TABLE_HABIT, KEY_FOLLOWING_FROM, System.currentTimeMillis(), KEY_ID, habit.id);
            database.execSQL(setFollowingCounter);
            Log.i(TAG, setFollowingCounter);
        }

        database.close();
        isSame = false;
    }

    @Override
    public Habit getHabit(long id) {
        Log.w("Database", "getHabitDetailsAsList()");

        String selectQuery = String.format("SELECT * FROM %s WHERE %s = %d", TABLE_HABIT, KEY_ID, id);
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        Habit temp = cursorToHabit(cursor);

        if (BuildConfig.DEBUG) {
            Log.d(TAG, temp.toString());
        }

        cursor.close();
        database.close();

        return temp;
    }

    @Override
    public void move(int fromPosition, int toPosition) {
        SQLiteDatabase database = getWritableDatabase();

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

    private static int daysTillNow(Habit habit) {
        Calendar updateDate = Calendar.getInstance();
        updateDate.set(habit.getMarkerUpdatedYear(), habit.getMarkerUpdatedMonth(), habit.getMarkerUpdatedDay());

        long millis1 = System.currentTimeMillis();
        long millis2 = updateDate.getTimeInMillis();
        long diff = millis2 - millis1;
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Not marked for " + diff / 86400000);
        }
        return (int) (diff / 86400000);
    }

    private static boolean mustHaveFollowed(Habit habit) {
        int daysTillNow = daysTillNow(habit);
        int[] freq = habit.getFrequencyArray();
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Must have followed = " + (daysTillNow > freq[freq.length - 1]));
        }
        return daysTillNow > freq[freq.length - 1];
    }
}
