package com.doapps.habits.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import java.util.ArrayList;
import java.util.List;

/**
 * The ready program which user can use
 */
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

  /**
   * Instantiates a new Program.
   *
   * @param id the id
   * @param title the title
   * @param percent the percent
   * @param habitId the habit id program is connected to
   * @param achievements the achievements
   */
  public Program(int id, String title, String percent, long habitId,
      List<Achievement> achievements) {
    this.id = id;
    this.title = title;
    this.percent = percent;
    this.habitId = habitId;
    this.achievements = achievements;
  }

  /**
   * Instantiates a new Program.
   *
   * @param id the id
   * @param title the title
   * @param percent the percent
   * @param habitId the habit id program is connected to
   */
  public Program(int id, String title, String percent, long habitId) {
    this.id = id;
    this.title = title;
    this.percent = percent;
    this.habitId = habitId;
    this.achievements = new ArrayList<>();
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
   * Gets percent.
   *
   * @return the percent
   */
  public String getPercent() {
    return percent;
  }

  /**
   * Sets percent.
   *
   * @param percent the percent
   */
  public void setPercent(String percent) {
    this.percent = percent;
  }

  /**
   * Gets habit id.
   *
   * @return the habit id
   */
  public long getHabitId() {
    return habitId;
  }

  /**
   * Sets habit id.
   *
   * @param habitId the habit id
   */
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