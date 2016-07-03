package com.dohabit.models;

public interface MovableItem {

    /* Called when item is moved from fromPosition to toPosition */
    void onItemMove(int fromPosition, int toPosition);

    /* Called when item is dismissed */
    void onItemDismiss(int position);
}