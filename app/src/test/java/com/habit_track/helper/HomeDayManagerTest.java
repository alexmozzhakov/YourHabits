package com.habit_track.helper;

import com.habit_track.adapter.TimeLineAdapter;
import com.habit_track.models.Habit;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class HomeDayManagerTest {
    @Test
    public void filterListForSat() throws Exception {
        List<Habit> list = new ArrayList<>();
        list.add(new Habit(1, "once", "", false, 12, 12, 12, 60, 30, 30, 101));
        list.add(new Habit(1, "once a week", "", false, 12, 12, 12, 60, 30, 30, 107));
        list.add(new Habit(1, "every saturday", "", false, 12, 12, 12, 60, 30, 30, 7007));
        list.add(new Habit(1, "every sunday and saturday", "", false, 12, 12, 12, 60, 30, 30, 10701));
        list.add(new Habit(1, "every friday", "", false, 12, 12, 12, 60, 30, 30, 6007));
        list.add(new Habit(1, "once a year", "", false, 12, 12, 12, 60, 30, 30, 10365));
        TimeLineAdapter adapter = new TimeLineAdapter(list);
        HomeDayManager manager = new HomeDayManager(adapter);
        manager.filterListForSat(list);
        for (Habit habit : list) {
            System.out.println(habit.title);
        }
        Assert.assertTrue(adapter.getList().size() == list.size());


    }

}