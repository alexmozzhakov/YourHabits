package com.habit_track.app;

public interface MovableItem {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}