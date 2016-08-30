package com.doapps.habits.models;

@FunctionalInterface
public interface OnItemSelectedListener<T> {
    /**
     * Callback method to be invoked when an item has been selected.
     * @param item An item that is selected
     */
    void onItemSelected(T item);
}
