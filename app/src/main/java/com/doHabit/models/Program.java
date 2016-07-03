package com.dohabit.models;

import java.util.List;

public class Program {
    private final int id;
    private final String title;
    private final String percent;
    private final long habitId;
    private final List<Achievement> achievements;

    public Program(int id, String title, String percent, long habitId, List<Achievement> achievements) {
        this.id = id;
        this.title = title;
        this.percent = percent;
        this.habitId = habitId;
        this.achievements = achievements;
    }

}