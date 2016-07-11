package com.doapps.habits.models;

import java.util.List;

public interface UpdatableList<T> {
    void updateList(final List<T> data);
}
