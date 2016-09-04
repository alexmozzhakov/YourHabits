package com.doapps.habits.models;

import java.util.Calendar;
import java.util.List;

public interface IHabitsDatabase {

    void updateHabit(Habit habit, int done);

    Habit getHabit(long id);

    void delete(int position);

    void move(int fromPosition, int toPosition);

    List<Habit> getHabitDetailsAsList();

    long addHabit(String name, String question, int time, Calendar upd, int cost, int... frequency);

    long addHabit(String name, String question, int time, Calendar upd, int cost, String frequency);

    boolean isEmpty();
}
