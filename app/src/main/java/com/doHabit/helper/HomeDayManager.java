package com.dohabit.helper;

import android.util.Log;

import com.dohabit.adapter.TimeLineAdapter;
import com.dohabit.models.Habit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HomeDayManager {
    private final TimeLineAdapter timeLineAdapter;

    public HomeDayManager(final TimeLineAdapter timeLineAdapter) {
        this.timeLineAdapter = timeLineAdapter;
    }

    public void updateListByDay(final List<Habit> habitList, final int dayOfWeek) {
        final List<Habit> dayHabits = new ArrayList<>(habitList);
        filterListByDay(dayHabits, String.valueOf(dayOfWeek));
        timeLineAdapter.updateList(dayHabits);
    }

    @SuppressWarnings({"CallToStringEquals", "LocalVariableOfConcreteClass"})
    static void filterListByDay(final Iterable<Habit> satHabits, final String dayOfWeek) {
        final Iterator<Habit> habitIterator = satHabits.iterator();
        while (habitIterator.hasNext()) {
            final Habit habit = habitIterator.next();
            if (habit.getFrequency() == 0) {
                Log.w("filterListByDay()", "frequency not set");
            } else {
                final String[] freq = String.valueOf(habit.getFrequency()).split("0");
                if (!("1".equals(freq[0]) && freq.length > 2) && // once type
                        habit.getFrequency() != 101) { // every day

                    boolean today = false;
                    for (int j = 0; j < freq.length - 1; j++) {
                        if (freq[j].equals(dayOfWeek)) {
                            today = true;
                            break;
                        }
                    }
                    if (!today) {
                        habitIterator.remove();
                    }
                }
            }
        }
    }
}
