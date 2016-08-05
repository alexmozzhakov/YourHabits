package com.doapps.habits.models;

@FunctionalInterface
public interface OnItemSelectedListener<T> {
    void onItemSelected(T item);
}
