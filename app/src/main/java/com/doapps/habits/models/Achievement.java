package com.doapps.habits.models;

import java.util.List;

public class Achievement {
    private final int rating;
    private final List<String> templates;

    public Achievement(int rating, List<String> templates) {
        this.rating = rating;
        this.templates = templates;
    }
}
