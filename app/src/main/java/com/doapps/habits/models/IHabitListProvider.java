package com.doapps.habits.models;

import java.util.List;

public interface IHabitListProvider extends IListProvider {
    /**
     * Obtains list of habits
     */
    @Override
    List<Habit> getList();
}
