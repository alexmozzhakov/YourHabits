package com.doapps.habits.adapter

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.doapps.habits.R
import com.doapps.habits.helper.HabitListManager
import com.doapps.habits.listeners.EmptyListListener
import com.doapps.habits.models.Habit
import com.doapps.habits.view.holders.HabitViewHolder
import java.util.*

class HabitRecycleAdapter(private val movableHabitList: HabitListManager)
  : RecyclerView.Adapter<HabitViewHolder>(), IMovableListAdapter {

  /**
   * A list of [com.doapps.habits.models.Habit]s for binding
   */
  private val habitList: MutableList<Habit> = movableHabitList.list

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.habit_list_item, parent, false)
    return HabitViewHolder(view)
  }

  override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
    val habit = habitList[position]

    holder.titleTextView.text = habit.title
    holder.checkBox.setOnClickListener {
      habit.isDoneMarker = holder.checkBox.isChecked
      movableHabitList.update(habit)
      holder.titleTextView.setTextColor(if (habit.isDoneMarker) Color.GRAY else Color.BLACK)
      holder.setProgressBar(habit)
    }
    holder.titleTextView.setTextColor(if (habit.isDoneMarker) Color.GRAY else Color.BLACK)
    holder.setProgressBar(habit)

    val calendar = Calendar.getInstance()

    holder.checkBox.isChecked = habit.isDone(
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.YEAR))

  }

  /**
   * The function to return number of items in list
   * @return Number of items in list
   */
  override fun getItemCount() = habitList.size

  /**
   * Handles item dismissing
   * @param position item position to be dismissed
   */
  override fun onItemDismiss(position: Int) {
    movableHabitList.onItemDismiss(position)
    habitList.removeAt(position)
    EmptyListListener.listener.isEmpty(habitList.size == 0)
    notifyItemRemoved(position)
  }

  /**
   * The function to handle item moving in list
   * @param fromPosition Item start position
   * @param toPosition Item finish position
   */
  override fun onItemMove(fromPosition: Int, toPosition: Int) {
    Collections.swap(habitList, fromPosition, toPosition)
    val fromPositionId = habitList[fromPosition].id
    habitList[fromPosition].id = habitList[toPosition].id
    habitList[toPosition].id = fromPositionId
    movableHabitList.onItemMove(habitList[fromPosition], habitList[toPosition])
    notifyItemMoved(fromPosition, toPosition)
  }
}

