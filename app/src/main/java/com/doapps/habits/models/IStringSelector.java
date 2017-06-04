package com.doapps.habits.models;

import com.doapps.habits.slider.swipeselector.SwipeAdapter;

public interface IStringSelector {
    /**
     * Register a callback to be invoked when an item in this selector has been clicked.
     * @param listener A listener to string selection
     */
    void setOnItemSelectedListener(IOnItemSelectedListener<String> listener);

    /**
     * Returns the adapter currently associated with this selector.
     * @return An adapter of items
     */
    SwipeAdapter getAdapter();

    /**
     * @param strings A list of string for Selector
     */
    void setItems(String... strings);
}
