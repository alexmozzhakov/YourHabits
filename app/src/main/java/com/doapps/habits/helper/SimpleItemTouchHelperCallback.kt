package com.doapps.habits.helper

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

import com.doapps.habits.adapter.IMovableListAdapter

class SimpleItemTouchHelperCallback(private val mAdapter: IMovableListAdapter) : ItemTouchHelper.Callback() {
  override fun isLongPressDragEnabled() = true
  override fun isItemViewSwipeEnabled() = true

  /**
   * Defines movement directions
   */
  override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
    val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
    val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
    return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
  }

  /**
   * Handles item move event
   */
  override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                      target: RecyclerView.ViewHolder): Boolean {
    mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
    return true
  }

  /**
   * Handles item swipe event
   */
  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) =
      mAdapter.onItemDismiss(viewHolder.adapterPosition)

}