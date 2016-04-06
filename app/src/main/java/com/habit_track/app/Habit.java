package com.habit_track.app;

import android.util.Log;

import java.util.Calendar;

public class Habit {
    public String title;
    public String description;
    public boolean doneMarker;
    public int markerUpdatedDay;
    public int markerUpdatedMonth;
    public int markerUpdatedYear;
    public int time;
    public int id;

    public Habit(String title) {
        super();
        this.title = title;
        this.doneMarker = false;
        this.time=60;
    }

    public void setDoneMarker(boolean doneMarker) {
        this.doneMarker = doneMarker;
        Calendar cal = Calendar.getInstance();
        //cal.setTime(date);
        //Calendar.get(Calendar.MONTH);


        //Log.i("cal ", ""+cal.get(Calendar.DATE));
        this.markerUpdatedDay = cal.get(Calendar.DATE);
        this.markerUpdatedMonth = cal.get(Calendar.MONTH);
        this.markerUpdatedYear = cal.get(Calendar.YEAR);
        Log.i("doneMarker ", ""+toString());
    }

    public boolean isDone(int markerUpdatedDay, int markerUpdatedMonth, int markerUpdatedYear) {
        return (markerUpdatedDay == this.markerUpdatedDay &&
                markerUpdatedMonth == this.markerUpdatedMonth &&
                markerUpdatedYear == this.markerUpdatedYear &&
                doneMarker);
    }

    @Override
    public String toString() {
        return "Habit{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", doneMarker=" + doneMarker +
                ", markerUpdatedDay=" + markerUpdatedDay +
                ", markerUpdatedMonth=" + markerUpdatedMonth +
                ", markerUpdatedYear=" + markerUpdatedYear +
                ", time=" + time +
                '}';
    }
}
