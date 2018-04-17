package com.doapps.habits.models

import com.doapps.habits.slider.swipeselector.SwipeAdapter

/**
 * An interface for string selectors
 */
interface IStringSelector {
  /**
   * Register a callback to be invoked when an item in this selector has been clicked.
   * @param listener A listener to string selection
   */
  fun setOnItemSelectedListener(listener: IOnItemSelectedListener<String>)

  /**
   * Returns the adapter currently associated with this selector.
   * @return An adapter of items
   */
  val adapter: SwipeAdapter

  /**
   * @param strings A list of string for Selector
   */
  fun setItems(vararg strings: String)
}
