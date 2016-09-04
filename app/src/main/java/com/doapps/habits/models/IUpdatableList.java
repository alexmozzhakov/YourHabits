package com.doapps.habits.models;

import java.util.List;

@FunctionalInterface
public interface IUpdatableList<T> {

    /**
     * Updates list with some data
     * @param data List, which will be updated on function call
     */
    void updateList(List<T> data);
}
