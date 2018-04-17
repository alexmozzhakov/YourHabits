package com.doapps.habits.helper

/**
 * An interface for any item that can progress
 */
interface Progressing {
  /**
   * Set a specific progress value
   *
   * @param progress the value to set
   */
  fun setProgress(progress: Int)
}