package com.doapps.habits.models;

public interface HabitDatabaseMovableListProvider extends HabitListProvider, MovableList{
    HabitsDatabase getDatabase();
}
