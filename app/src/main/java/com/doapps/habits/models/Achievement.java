package com.doapps.habits.models;

import java.util.List;

public class Achievement {
    private final int rating;
    private final List<String> templates;

    public Achievement(final int rating, final List<String> templates) {
        this.rating = rating;
        this.templates = templates;
    }
}
