package com.doapps.habits.models

/**
 * Manages days changes
 */
interface IDayManager {
  /**
   * Updates lists content by day of week
   * @param dayOfWeek Number of day from 1 to 7
   */
  fun updateListByDayOfWeek(dayOfWeek: Int)

  /**
   * Updates lists content for today
   */
  fun updateForToday()
}
