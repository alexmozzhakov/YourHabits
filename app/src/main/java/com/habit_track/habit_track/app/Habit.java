package com.habit_track.habit_track.app;

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

    public Habit(final String title, int position) {
        super();
        this.title = title;
        this.doneMarker = false;
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
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", doneMarker=" + doneMarker +
                ", markerUpdatedDay=" + markerUpdatedDay +
                ", markerUpdatedMonth=" + markerUpdatedMonth +
                ", markerUpdatedYear=" + markerUpdatedYear +
                ", time=" + time +
                ", id=" + id +
                '}';
    }
}
