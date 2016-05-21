package com.habit_track.models;

import java.util.Calendar;

public class Habit {

    //Constant Habit values
    public final int id;
    public final String title;
    private final String description;

    // Changeable Habit values
    private boolean doneMarker;
    public int markerUpdatedDay;
    public int markerUpdatedMonth;
    public int markerUpdatedYear;
    public int time;
    private int daysFollowing;
    private int cost;
    private int frequency;

    public Habit(int id, String title, String description, boolean doneMarker, int markerUpdatedDay,
                 int markerUpdatedMonth, int markerUpdatedYear, int time,
                 int followingFrom, int cost, int frequency) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.doneMarker = doneMarker;
        this.markerUpdatedDay = markerUpdatedDay;
        this.markerUpdatedMonth = markerUpdatedMonth;
        this.markerUpdatedYear = markerUpdatedYear;
        this.time = time;
        this.daysFollowing = followingFrom;
        this.cost = cost;
        this.frequency = frequency;
    }

    public void setDoneMarker(final boolean doneMarker) {
        this.doneMarker = doneMarker;
        if (doneMarker) {
            final Calendar calendar = Calendar.getInstance();
            this.markerUpdatedDay = calendar.get(Calendar.DATE);
            this.markerUpdatedMonth = calendar.get(Calendar.MONTH);
            this.markerUpdatedYear = calendar.get(Calendar.YEAR);
        }
    }

    public boolean isDone(final int markerUpdatedDay, final int markerUpdatedMonth, final int markerUpdatedYear) {
        return markerUpdatedDay == this.markerUpdatedDay &&
                markerUpdatedMonth == this.markerUpdatedMonth &&
                markerUpdatedYear == this.markerUpdatedYear &&
                doneMarker;
    }

    @Override
    public String toString() {
        return "Habit{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", doneMarker=" + doneMarker +
                ", markerUpdatedDay=" + markerUpdatedDay +
                ", markerUpdatedMonth=" + markerUpdatedMonth +
                ", markerUpdatedYear=" + markerUpdatedYear +
                ", time=" + time +
                ", daysFollowing=" + daysFollowing +
                ", cost=" + cost +
                ", frequency=" + frequency +
                '}';
    }
}
