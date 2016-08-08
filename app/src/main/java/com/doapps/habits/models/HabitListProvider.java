package com.doapps.habits.models;

import java.util.List;

public interface HabitListProvider extends ListProvider {
    /**
     * Obtains list of {@link Habit)
     */
    @Override
    List<Habit> getList();
}
