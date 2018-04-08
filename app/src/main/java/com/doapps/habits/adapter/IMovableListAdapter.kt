package com.doapps.habits.adapter

interface IMovableListAdapter {

  /**
   * Called when item is moved from fromPosition to toPosition
   * @param fromPosition Item start position
   * @param toPosition Item finish position
   */
  fun onItemMove(fromPosition: Int, toPosition: Int)

  /** Called when item is dismissed *
   * @param position Position of an item to be dismissed
   */
  fun onItemDismiss(position: Int)
}