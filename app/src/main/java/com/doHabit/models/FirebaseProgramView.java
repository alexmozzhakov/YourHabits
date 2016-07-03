package com.dohabit.models;

import com.google.firebase.database.DataSnapshot;

public class FirebaseProgramView implements ProgramViewProvider {
    private final String title;
    private final String percent;
    private final String description;
    private final String imageLink;
    private final DataSnapshot snapshot;

    public FirebaseProgramView(final DataSnapshot snapshot) {
        this.snapshot = snapshot;
        title = snapshot.child("name").getValue(String.class);
        percent = String.format("%s SUCCESS", snapshot.child("success").getValue(String.class));
        description = snapshot.child("description").getValue(String.class);
        imageLink = String.format("http://habbitsapp.esy.es/img_progs/%s.jpg",
                snapshot.child("image").getValue(String.class));
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getPercent() {
        return percent;
    }

    @Override
    public String getImageLink() {
        return imageLink;
    }

    @Override
    public DataSnapshot getSnapshot() {
        return snapshot;
    }
}
