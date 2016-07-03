package com.dohabit.models;

import java.util.Calendar;

public class Habit {

    //Constant Habit values
    public final int id;
    public final String title;
    private final String question;
    private final int time;
    private final int cost;
    private final int frequency;

    // Changeable Habit values
    private boolean doneMarker;
    public int markerUpdatedDay;
    public int markerUpdatedMonth;
    public int markerUpdatedYear;
    private final int daysFollowing;

    public Habit(int id, String title, String question, boolean doneMarker, int markerUpdatedDay,
                 int markerUpdatedMonth, int markerUpdatedYear, int time,
                 int followingFrom, int cost, int frequency) {

        this.id = id;
        this.title = title;
        this.question = question;
        this.doneMarker = doneMarker;
        this.markerUpdatedDay = markerUpdatedDay;
        this.markerUpdatedMonth = markerUpdatedMonth;
        this.markerUpdatedYear = markerUpdatedYear;
        this.time = time;
        daysFollowing = followingFrom;
        this.cost = cost;
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
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

    public boolean isDone(int markerUpdatedDay, int markerUpdatedMonth, int markerUpdatedYear) {
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
                ", question='" + question + '\'' +
                ", time=" + time +
                ", cost=" + cost +
                ", frequency=" + frequency +
                ", doneMarker=" + doneMarker +
                ", markerUpdatedDay=" + markerUpdatedDay +
                ", markerUpdatedMonth=" + markerUpdatedMonth +
                ", markerUpdatedYear=" + markerUpdatedYear +
                ", daysFollowing=" + daysFollowing +
                '}';
    }
}
