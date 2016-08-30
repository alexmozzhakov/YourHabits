package com.doapps.habits.models;

import java.util.List;

@FunctionalInterface
public interface UpdatableList<T> {

    /**
     * @param data is list, which will be updated on function call
     */
    void updateList(List<T> data);
}
