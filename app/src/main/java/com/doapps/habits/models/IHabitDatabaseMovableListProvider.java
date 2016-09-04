package com.doapps.habits.models;

import com.doapps.habits.helper.HabitListManager;

/**
 * Interface specifies container with synced with database {@link IHabitsDatabase} list with item move support like {@link HabitListManager}
 */
public interface IHabitDatabaseMovableListProvider extends IHabitListProvider, IMovableList {
    IHabitsDatabase getDatabase();
}
