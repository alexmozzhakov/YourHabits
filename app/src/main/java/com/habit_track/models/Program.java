package com.habit_track.models;

import java.util.ArrayList;

public class Program {
    private final int id;
    private final String title;
    private final String percent;
    private final long habitId;
    private final ArrayList<Achievement> achievements;

    public Program(int id, String title, String percent, long habitId, ArrayList<Achievement> achievements) {
        this.id = id;
        this.title = title;
        this.percent = percent;
        this.habitId = habitId;
        this.achievements = achievements;
    }

}