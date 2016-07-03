package com.dohabit.models;

import com.google.firebase.database.DataSnapshot;

public interface ProgramViewProvider {
    String getTitle();
    String getDescription();
    String getPercent();
    String getImageLink();
    DataSnapshot getSnapshot();
}
