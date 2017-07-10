package com.doapps.habits.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import java.util.Arrays;
import java.util.Calendar;

@Entity(tableName = "habits")
public final class Habit {

  private final int[] frequencyArray;
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

  public Habit(String title, String question, boolean doneMarker,
      int markerUpdatedDay, int markerUpdatedMonth, int markerUpdatedYear,
      int time, long followingFrom, int cost, int... frequencyArray) {

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
    this.frequencyArray = frequencyArray;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int[] getFrequencyArray() {
    return frequencyArray;
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
        ", frequencyArray=" + Arrays.toString(frequencyArray) +
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
    }
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
}