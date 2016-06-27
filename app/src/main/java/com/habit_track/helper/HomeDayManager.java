package com.habit_track.helper;

import android.util.Log;

import com.habit_track.adapter.TimeLineAdapter;
import com.habit_track.models.Habit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeDayManager {
    private final TimeLineAdapter timeLineAdapter;

    public HomeDayManager(final TimeLineAdapter timeLineAdapter) {
        this.timeLineAdapter = timeLineAdapter;
    }

    public void sun() {
        return;
    }

    // TODO: 26/06/2016 write cases for every day of week
    public void mon() {
        return;
    }

    public void tue() {
        return;
    }

    public void wed() {
        return;
    }

    public void thu() {
        return;
    }

    public void fri() {
        return;
    }

    public void updateListForSat(List<Habit> habitList) {
        final List<Habit> satHabits = new ArrayList<>(habitList);
        filterListForSat(satHabits);
        timeLineAdapter.updateList(satHabits);
    }

    void filterListForSat(List<Habit> satHabits) {
        for (int i = 0; i < satHabits.size(); i++) {
            String[] freq = String.valueOf(satHabits.get(i).getFrequency()).split("0");
            if (freq.length > 3) {
                Log.i("filterListForSat()", Arrays.toString(freq));
                int[] frq = new int[freq.length];
                for (int j = 0; j < freq.length && j < frq.length; j++) {
                    frq[j] = Integer.parseInt(freq[j]);
                }
                boolean once = frq[0] == 1 && frq[frq.length - 1] == 0;
                boolean everySaturday = false;

                if (frq[0] == 7 && frq[1] == 0 && frq[2] == 0) {
                    everySaturday = true;
                } else {
                    for (int j = 1; j < frq.length - 1; j++) {
                        if (frq[j] == 7 && frq[j + 1] == 0) {
                            everySaturday = true;
                        }
                    }
                }

                boolean everyDay = frq[0] == frq[frq.length - 1] && frq.length == 3;
                if (!everyDay && !once && !everySaturday) {
                    satHabits.remove(i);
                }
            }
        }
    }
}
