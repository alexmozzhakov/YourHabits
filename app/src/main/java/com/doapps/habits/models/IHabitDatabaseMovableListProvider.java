package com.doapps.habits.models;

/**
 * Interface specifies container with synced with database {@link IHabitsDatabase}
 * list with item move support
 */
public interface IHabitDatabaseMovableListProvider extends IHabitListProvider, IMovableList {
    IHabitsDatabase getDatabase();
}
