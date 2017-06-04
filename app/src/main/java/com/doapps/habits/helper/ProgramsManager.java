package com.doapps.habits.helper;

import android.util.SparseArray;

import com.doapps.habits.models.Program;

public interface ProgramsManager {
    SparseArray<Program> getProgramsAdded();
}
