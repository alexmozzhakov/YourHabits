package com.habit_track.models;

public class ProgramView {
    public final String title;
    public final String percent;
    private final String description;

    public ProgramView(String title, String percent, String description) {
        this.title = title;
        this.percent = percent;
        this.description = description;
    }
}
