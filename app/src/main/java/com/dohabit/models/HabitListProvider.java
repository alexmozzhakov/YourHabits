package com.dohabit.models;

import java.util.List;

@FunctionalInterface
public interface HabitListProvider extends ListProvider {
    @Override
    List<Habit> getList();
}
