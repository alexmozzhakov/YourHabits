package com.habit_track.helper;

import com.habit_track.adapter.TimeLineAdapter;
import com.habit_track.models.Habit;

import java.util.ArrayList;
import java.util.List;

public class HomeDayManager {
    private final List<Habit> habitList;

    public HomeDayManager(final List<Habit> habitList) {
        this.habitList = habitList;
    }

    public List<Habit> sun() {
        return habitList;
    }

    // TODO: 26/06/2016 write cases for every day of week
    public List<Habit> mon() {
        return habitList;
    }

    public List<Habit> tue() {
        return habitList;
    }

    public List<Habit> wed() {
        return habitList;
    }

    public List<Habit> thu() {
        return habitList;
    }

    public List<Habit> fri() {
        return habitList;
    }

    public void updateListForSat(final TimeLineAdapter timeLineAdapter) {
        final List<Habit> satHabits = new ArrayList<>(habitList);
        for (int i = 0; i < satHabits.size(); i++) {
            boolean everyDay = satHabits.get(i).getFrequency() % 10 ==
                    satHabits.get(i).getFrequency() / 100;
            if (!everyDay) {
                satHabits.remove(i);
            }
        }
        timeLineAdapter.updateList(satHabits);
    }
}
