package com.doapps.habits.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Program {

  @Ignore
  private final List<Achievement> achievements;
  @PrimaryKey
  private int id;
  @ColumnInfo(name = "title")
  private String title;
  @ColumnInfo(name = "percent")
  private String percent;
  @ColumnInfo(name = "habit_id")
  private long habitId;

  public Program(int id, String title, String percent, long habitId,
      List<Achievement> achievements) {
    this.id = id;
    this.title = title;
    this.percent = percent;
    this.habitId = habitId;
    this.achievements = achievements;
  }

  public Program(int id, String title, String percent, long habitId) {
    this.id = id;
    this.title = title;
    this.percent = percent;
    this.habitId = habitId;
    this.achievements = new ArrayList<>();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getPercent() {
    return percent;
  }

  public void setPercent(String percent) {
    this.percent = percent;
  }

  public long getHabitId() {
    return habitId;
  }

  public void setHabitId(long habitId) {
    this.habitId = habitId;
  }

  @Override
  public String toString() {
    return "Program{" +
        "id=" + id +
        ", title='" + title + '\'' +
        ", percent='" + percent + '\'' +
        ", habitId=" + habitId +
        ", achievements=" + achievements +
        '}';
  }
}