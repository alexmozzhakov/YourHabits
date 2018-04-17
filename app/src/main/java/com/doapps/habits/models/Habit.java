package com.doapps.habits.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;
import com.doapps.habits.BuildConfig;
import java.util.Arrays;
import java.util.Calendar;

/**
 * The habit data object
 */
@Entity(tableName = "habits")
public final class Habit {

  private static final String TAG = Habit.class.getSimpleName();
  // TODO: 7/15/17 document frequency array behavior
  private final int[] frequency;
  @PrimaryKey(autoGenerate = true)
  private int id;
  //Constant Habit values
  private String title;
  private String question;
  private int time;
  private int cost;
  @ColumnInfo(name = "program_id")
  private int programId;
  // Changeable Habit values
  private boolean doneMarker;
  private int markerUpdatedDay;
  private int markerUpdatedMonth;
  private int markerUpdatedYear;
  private long followingFrom;
  private int doneCounter = 0;

  /**
   * Instantiates a new Habit.
   *
   * @param title the title
   * @param question the question
   * @param doneMarker the done marker
   * @param markerUpdatedDay the marker of day last updated
   * @param markerUpdatedMonth the marker of month last updated
   * @param markerUpdatedYear the marker of year last updated
   * @param time the time for habit to take
   * @param followingFrom the date user is following the habit from
   * @param cost the cost for each habit cycle
   * @param frequency the frequency array of habit in specific format
   */
  public Habit(String title, String question, boolean doneMarker,
      int markerUpdatedDay, int markerUpdatedMonth, int markerUpdatedYear,
      int time, long followingFrom, int cost, int... frequency) {

    this.title = title;
    this.question = question;
    this.programId = -1;
    this.doneMarker = doneMarker;
    this.markerUpdatedDay = markerUpdatedDay;
    this.markerUpdatedMonth = markerUpdatedMonth;
    this.markerUpdatedYear = markerUpdatedYear;
    this.time = time;
    this.followingFrom = followingFrom;
    this.cost = cost;
    this.frequency = frequency;
  }

  /**
   * Gets title.
   *
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets title.
   *
   * @param title the title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Get frequency int [ ].
   *
   * @return the int [ ]
   */
  public int[] getFrequency() {
    return frequency;
  }

  /**
   * Is done boolean.
   *
   * @param markerUpdatedDay the marker of day last updated
   * @param markerUpdatedMonth the marker of month last updated
   * @param markerUpdatedYear the marker of year last updated
   * @return the boolean
   */
  public boolean isDone(int markerUpdatedDay, int markerUpdatedMonth, int markerUpdatedYear) {
    return markerUpdatedDay == this.markerUpdatedDay &&
        markerUpdatedMonth == this.markerUpdatedMonth &&
        markerUpdatedYear == this.markerUpdatedYear &&
        doneMarker;
  }


  @Override
  public String toString() {
    return "Habit{" +
        "title='" + title + '\'' +
        ", question='" + question + '\'' +
        ", time=" + time +
        ", cost=" + cost +
        ", frequency=" + Arrays.toString(frequency) +
        ", programId=" + programId +
        ", doneMarker=" + doneMarker +
        ", markerUpdatedDay=" + markerUpdatedDay +
        ", markerUpdatedMonth=" + markerUpdatedMonth +
        ", markerUpdatedYear=" + markerUpdatedYear +
        ", followingFrom=" + followingFrom +
        '}';
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Gets question.
   *
   * @return the question
   */
  public String getQuestion() {
    return question;
  }

  /**
   * Sets question.
   *
   * @param question the question
   */
  public void setQuestion(String question) {
    this.question = question;
  }

  /**
   * Gets time.
   *
   * @return the time
   */
  public int getTime() {
    return time;
  }

  /**
   * Sets time.
   *
   * @param time the time
   */
  public void setTime(int time) {
    this.time = time;
  }

  /**
   * Gets cost.
   *
   * @return the cost
   */
  public int getCost() {
    return cost;
  }

  /**
   * Sets cost.
   *
   * @param cost the cost
   */
  public void setCost(int cost) {
    this.cost = cost;
  }

  /**
   * Gets program id.
   *
   * @return the program id
   */
  public int getProgramId() {
    return programId;
  }

  /**
   * Sets program id.
   *
   * @param programId the program id
   */
  public void setProgramId(int programId) {
    this.programId = programId;
  }

  /**
   * Is done marker boolean.
   *
   * @return the boolean
   */
  public boolean isDoneMarker() {
    return doneMarker;
  }

  /**
   * Sets done marker.
   *
   * @param doneMarker the done marker
   */
  public void setDoneMarker(boolean doneMarker) {
    this.doneMarker = doneMarker;
    if (doneMarker) {
      Calendar calendar = Calendar.getInstance();
      markerUpdatedDay = calendar.get(Calendar.DATE);
      markerUpdatedMonth = calendar.get(Calendar.MONTH);
      markerUpdatedYear = calendar.get(Calendar.YEAR);
      doneCounter++;
    } else {
      doneCounter--;
    }
  }

  /**
   * Gets done counter.
   *
   * @return the done counter
   */
  public int getDoneCounter() {
    return doneCounter;
  }

  /**
   * Sets done counter.
   *
   * @param doneCounter the done counter
   */
  public void setDoneCounter(int doneCounter) {
    this.doneCounter = doneCounter;
  }

  /**
   * Gets marker updated day.
   *
   * @return the marker updated day
   */
  public int getMarkerUpdatedDay() {
    return markerUpdatedDay;
  }

  /**
   * Sets marker updated day.
   *
   * @param markerUpdatedDay the marker updated day
   */
  public void setMarkerUpdatedDay(int markerUpdatedDay) {
    this.markerUpdatedDay = markerUpdatedDay;
  }

  /**
   * Gets marker updated month.
   *
   * @return the marker updated month
   */
  public int getMarkerUpdatedMonth() {
    return markerUpdatedMonth;
  }

  /**
   * Sets marker updated month.
   *
   * @param markerUpdatedMonth the marker updated month
   */
  public void setMarkerUpdatedMonth(int markerUpdatedMonth) {
    this.markerUpdatedMonth = markerUpdatedMonth;
  }

  /**
   * Gets marker updated year.
   *
   * @return the marker updated year
   */
  public int getMarkerUpdatedYear() {
    return markerUpdatedYear;
  }

  /**
   * Sets marker updated year.
   *
   * @param markerUpdatedYear the marker updated year
   */
  public void setMarkerUpdatedYear(int markerUpdatedYear) {
    this.markerUpdatedYear = markerUpdatedYear;
  }

  /**
   * Gets following from.
   *
   * @return the following from
   */
  public long getFollowingFrom() {
    return followingFrom;
  }

  /**
   * Sets following from.
   *
   * @param followingFrom the following from
   */
  public void setFollowingFrom(long followingFrom) {
    this.followingFrom = followingFrom;
  }

  /**
   * Is program boolean.
   *
   * @return the boolean
   */
  public boolean isProgram() {
    return programId == -1;
  }


  private int daysTillNow() {
    Calendar updateDate = Calendar.getInstance();
    updateDate.set(markerUpdatedYear, markerUpdatedMonth, markerUpdatedDay);

    long millis1 = System.currentTimeMillis();
    long millis2 = updateDate.getTimeInMillis();
    long diff = millis2 - millis1;
    if (BuildConfig.DEBUG) {
      Log.i(TAG, "Not marked for " + diff / 8.64e7);
    }
    return (int) (diff / 8.64e7);
  }

  /**
   * Must have followed boolean.
   *
   * @return the boolean
   */
  public boolean mustHaveFollowed() {
    int daysTillNow = daysTillNow();
    if (BuildConfig.DEBUG) {
      Log.i(TAG, "Must have followed = " + (daysTillNow > frequency[frequency.length - 1]));
    }
    return daysTillNow > frequency[frequency.length - 1];
  }

}