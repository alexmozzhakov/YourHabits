package com.habit_track.models;

import java.util.ArrayList;

public class Program {
    public final int id;
    public final String title;
    public final String percent;
    public final long habitId;
    public final ArrayList<Achievement> achievements;

    public Program(int id, String title, String percent, long habitId, ArrayList<Achievement> achievements) {
        this.id = id;
        this.title = title;
        this.percent = percent;
        this.habitId = habitId;
        this.achievements = achievements;
    }

}