package com.doapps.habits.models;

public interface DayManager {
    // Updates lists content for day from 1 to 7
    void updateListByDay(int dayOfWeek);
    // Updates lists content for today
    void updateForToday();
}
