package com.doapps.habits.helper;

import com.doapps.habits.models.Habit;
import com.doapps.habits.models.IDayManager;
import com.doapps.habits.models.IUpdatableList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeDayManager implements IDayManager, DueCounter {
    private final IUpdatableList<Habit> mTimeLineAdapter;
    private final List<Habit> mHabitList;
    private int dueCount;

    public HomeDayManager(IUpdatableList<Habit> timeLineAdapter, List<Habit> habitList) {
        mTimeLineAdapter = timeLineAdapter;
        mHabitList = habitList;
    }

    @Override
    public void updateListByDayOfWeek(int dayOfWeek) {
        List<Habit> dayHabits = new ArrayList<>(mHabitList);
        DayFilter.filterListByDay(dayHabits, dayOfWeek);
        dueCount = countDue(dayHabits);
        mTimeLineAdapter.updateList(dayHabits);
    }

    @Override
    public void updateForToday() {
        List<Habit> todayHabits = new ArrayList<>(mHabitList);
        DayFilter.filterListForToday(todayHabits);
        dueCount = countDue(todayHabits);
        mTimeLineAdapter.updateList(todayHabits);
    }

    private int countDue(List<Habit> list){
        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        int counter = 0;
        for (Habit habit : list) {
            if (!habit.isDone(date, month, year)) {
                counter++;
            }
        }
        return counter;
    }

    @Override
    public String getDueCount(){
        return String.valueOf(dueCount);
    }


}