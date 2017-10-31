package com.doapps.habits.models

abstract class IOnItemSelectedListener<in T> {

  /**
   * Callback method to be invoked when an item has been selected.
   * @param item An item that is selected
   */
  abstract fun onItemSelected(item: T)
}
