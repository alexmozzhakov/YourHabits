package com.doapps.habits.helper;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseManager {
    private static FirebaseDatabaseManager ourInstance;

    public static FirebaseDatabaseManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new FirebaseDatabaseManager();
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        return ourInstance;
    }

    private FirebaseDatabaseManager() {
    }

    public DatabaseReference getDatabase() {
        return FirebaseDatabase.getInstance().getReference();
    }
}
