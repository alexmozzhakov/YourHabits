package com.habit_track.models;

import java.util.ArrayList;

public class Achievement {
    public final int rating;
    public final ArrayList<String> templates;

    public Achievement(int rating, ArrayList<String> templates) {
        this.rating = rating;
        this.templates = templates;
    }
}
