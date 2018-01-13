package com.doapps.habits.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;
import com.doapps.habits.BuildConfig;
import java.util.Arrays;
import java.util.Calendar;

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
  private long followingFrom = 0;
  private int doneCounter = 0;

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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int[] getFrequency() {
    return frequency;
  }

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

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public int getCost() {
    return cost;
  }

  public void setCost(int cost) {
    this.cost = cost;
  }

  public int getProgramId() {
    return programId;
  }

  public void setProgramId(int programId) {
    this.programId = programId;
  }

  public boolean isDoneMarker() {
    return doneMarker;
  }

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

  public int getDoneCounter() {
    return doneCounter;
  }

  public void setDoneCounter(int doneCounter) {
    this.doneCounter = doneCounter;
  }

  public int getMarkerUpdatedDay() {
    return markerUpdatedDay;
  }

  public void setMarkerUpdatedDay(int markerUpdatedDay) {
    this.markerUpdatedDay = markerUpdatedDay;
  }

  public int getMarkerUpdatedMonth() {
    return markerUpdatedMonth;
  }

  public void setMarkerUpdatedMonth(int markerUpdatedMonth) {
    this.markerUpdatedMonth = markerUpdatedMonth;
  }

  public int getMarkerUpdatedYear() {
    return markerUpdatedYear;
  }

  public void setMarkerUpdatedYear(int markerUpdatedYear) {
    this.markerUpdatedYear = markerUpdatedYear;
  }

  public long getFollowingFrom() {
    return followingFrom;
  }

  public void setFollowingFrom(long followingFrom) {
    this.followingFrom = followingFrom;
  }

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

  public boolean mustHaveFollowed() {
    int daysTillNow = daysTillNow();
    if (BuildConfig.DEBUG) {
      Log.i(TAG, "Must have followed = " + (daysTillNow > frequency[frequency.length - 1]));
    }
    return daysTillNow > frequency[frequency.length - 1];
  }

}