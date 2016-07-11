package com.doapps.habits.helper;

import android.util.Log;

import com.doapps.habits.models.DayManager;
import com.doapps.habits.models.Habit;
import com.doapps.habits.models.UpdatableList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HomeDayManager implements DayManager<Habit> {
    private final UpdatableList<Habit> timeLineAdapter;

    public HomeDayManager(final UpdatableList<Habit> timeLineAdapter) {
        this.timeLineAdapter = timeLineAdapter;
    }

    @Override
    public void updateListByDay(final List<Habit> list, final int dayOfWeek) {
        final List<Habit> dayHabits = new ArrayList<>(list);
        filterListByDay(dayHabits, String.valueOf(dayOfWeek + 1));
        timeLineAdapter.updateList(dayHabits);
    }

    @Override
    @SuppressWarnings({"CallToStringEquals", "LocalVariableOfConcreteClass"})
    public void updateForToday(final List<Habit> list, final int dayOfWeek) {
        final List<Habit> todayHabits = new ArrayList<>(list);
        filterListForToday(todayHabits, String.valueOf(dayOfWeek + 1));
        timeLineAdapter.updateList(todayHabits);
    }

    public static void filterListForToday(final Iterable<Habit> todayHabits, final String dayOfWeek) {
        final Iterator<Habit> habitIterator = todayHabits.iterator();
        while (habitIterator.hasNext()) {
            final Habit habit = habitIterator.next();
            if (habit.getFrequency() == 0) {
                Log.w("filterListByDay()", "frequency not set");
            } else {
                final String[] freq = String.valueOf(habit.getFrequency()).split("0");
                if (freq.length > 2) { // not once type
                    boolean today = false;
                    for (int j = 0; j < freq.length - 1; j++) {
                        if (freq[j].equals(dayOfWeek)) {
                            today = true;
                            break;
                        }
                    }
                    if (!today) {
                        // if not found days markers
                        habitIterator.remove();
                    }
                }
            }
        }
    }

    @SuppressWarnings({"CallToStringEquals", "LocalVariableOfConcreteClass"})
    static void filterListByDay(final Iterable<Habit> dayHabits, final String dayOfWeek) {
        final Iterator<Habit> habitIterator = dayHabits.iterator();
        while (habitIterator.hasNext()) {
            final Habit habit = habitIterator.next();
            final String[] freq = String.valueOf(habit.getFrequency()).split("0");
            if (habit.getFrequency() == 0) {
                Log.w("filterListByDay()", "frequency not set");
            } else if (habit.getFrequency() != 101) {
                // if it's not every day
                if (freq.length > 2) {
                    boolean today = false;
                    for (int j = 0; j < freq.length - 1; j++) {
                        if (freq[j].equals(dayOfWeek)) {
                            today = true;
                            break;
                        }
                    }
                    if (!today) {
                        // if not found days markers
                        habitIterator.remove();
                    }
                } else {
                    // once type
                    habitIterator.remove();
                }
            }
        }
    }
}