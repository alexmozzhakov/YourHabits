package com.doapps.habits.models;

import java.util.Calendar;
import java.util.List;

public interface HabitsDatabase {

    void updateHabit(int id, int day, int month, int year,
                     int done);

    void delete(int position);

    void move(int fromPosition, int toPosition);

    List<Habit> getHabitDetailsAsList();

    long addHabit(String name, String question, int time, Calendar upd,
                  int followingFrom, int cost, int frequency);
}
