package com.doapps.habits.models;

import com.doapps.habits.slider.swipeselector.SwipeAdapter;

public interface StringSelector {
    void setOnItemSelectedListener(OnItemSelectedListener<String> listener);
    SwipeAdapter getAdapter();
    void setItems(String... strings);
}
