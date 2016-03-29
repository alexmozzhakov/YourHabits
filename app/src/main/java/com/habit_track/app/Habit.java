package com.habit_track.app;

import java.util.Calendar;
import java.util.Date;

public class Habit {
    public String title;
    public String description;
    public boolean doneMarker;
    public int markerUpdatedDay;
    public int markerUpdatedMonth;
    public int markerUpdatedYear;

    public Habit(String title, String description) {
        super();
        this.title = title;
        this.description = description;
        this.doneMarker = false;
    }

    public void setDoneMarker(boolean doneMarker, Date date) {
        this.doneMarker = doneMarker;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        markerUpdatedDay = cal.get(Calendar.DATE);
        markerUpdatedMonth = cal.get(Calendar.MONTH);
        markerUpdatedYear = cal.get(Calendar.YEAR);
    }

    public void setDone() {
        doneMarker = true;
    }

    private boolean isDone(int markerUpdatedDay, int markerUpdatedMonth, int markerUpdatedYear) {
        return (markerUpdatedDay == this.markerUpdatedDay &&
                markerUpdatedMonth == this.markerUpdatedMonth &&
                markerUpdatedYear == this.markerUpdatedYear &&
                doneMarker);
    }
}
