package com.doapps.habits.models;

import java.util.List;

public interface HabitListProvider extends ListProvider{
    @Override
    List<Habit> getList();
}
