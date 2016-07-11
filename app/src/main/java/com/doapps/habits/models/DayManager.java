package com.doapps.habits.models;

import java.util.List;

public interface DayManager<T> {
    void updateListByDay(List<T> list, int dayOfWeek);
    void updateForToday(List<T> list, int dayOfWeek);
}
