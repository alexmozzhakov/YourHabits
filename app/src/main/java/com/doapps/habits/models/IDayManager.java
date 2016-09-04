package com.doapps.habits.models;

/**
 * Manages days changes
 */
public interface IDayManager {
    /**
     * Updates lists content by day of week
     * @param dayOfWeek Number of day from 1 to 7
     */
    void updateListByDayOfWeek(int dayOfWeek);

    /**
     * Updates lists content for today
     */
    void updateForToday();
}
