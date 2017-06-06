package com.doapps.habits.helper;

import android.support.v4.app.FragmentManager;

import com.google.firebase.database.DataSnapshot;

public interface ClickableDataSnapshotFragment {
    void onClick(DataSnapshot dataSnapshot, FragmentManager fragmentManager);
}
