package com.doapps.habits.models;

import java.util.List;

@FunctionalInterface
public interface UpdatableList<T> {
    void updateList(final List<T> data);
}
