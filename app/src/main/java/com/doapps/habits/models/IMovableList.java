package com.doapps.habits.models;

public interface IMovableList {

    /* Called when item is moved from fromPosition to toPosition */
    void onItemMove(int fromPosition, int toPosition);

    /* Called when item is dismissed */
    void onItemDismiss(int position);
}