package com.doapps.habits.models

/**
 * An interface for item selection listeners
 */
interface IOnItemSelectedListener<in T> {

  /**
   * Callback method to be invoked when an item has been selected.
   * @param item An item that is selected
   */
  fun onItemSelected(item: T)
}
