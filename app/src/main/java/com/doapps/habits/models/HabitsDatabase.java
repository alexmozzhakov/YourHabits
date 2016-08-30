package com.doapps.habits.models;

import java.util.Calendar;
import java.util.List;

public interface HabitsDatabase {

    void updateHabit(Habit habit, int done);

    void delete(int position);

    void move(int fromPosition, int toPosition);

    List<Habit> getHabitDetailsAsList();

    void addHabit(String name, String question, int time, Calendar upd, int cost, int... frequency);

    long addHabit(String name, String question, int time, Calendar upd, int cost, String frequency);

    boolean isEmpty();
}
