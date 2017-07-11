package com.doapps.habits.adapter

import android.graphics.Color
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.doapps.habits.R
import com.doapps.habits.database.HabitsDatabase
import com.doapps.habits.helper.HabitListManager
import com.doapps.habits.listeners.EmptyListListener
import com.doapps.habits.models.Habit
import com.doapps.habits.view.holders.HabitViewHolder
import java.util.*

class HabitRecycleAdapter(private val movableHabitList: HabitListManager)
  : RecyclerView.Adapter<HabitViewHolder>(), IMovableListAdapter {

  private val habitList: MutableList<Habit> = movableHabitList.list
  private val habitsDatabase: HabitsDatabase = movableHabitList.database

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.habit_list_item, parent, false)
    return HabitViewHolder(view)
  }

  override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
    val habit = habitList[position]

    holder.titleTextView.text = habit.title
    holder.checkBox.setOnClickListener {
      if (holder.checkBox.isChecked) {
        habit.isDoneMarker = true
        UpdateTask(habitsDatabase).execute(habit)
        holder.titleTextView.setTextColor(Color.GRAY)
      } else {
        habit.isDoneMarker = false
        UpdateTask(habitsDatabase).execute(habit)
        holder.titleTextView.setTextColor(Color.BLACK)
      }
    }

    val calendar = Calendar.getInstance()

    holder.checkBox.isChecked = habit.isDone(
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.YEAR))

  }

  override fun getItemCount() = habitList.size

  override fun onItemDismiss(position: Int) {
    movableHabitList.onItemDismiss(position)
    habitList.removeAt(position)
    EmptyListListener.listener.isEmpty(habitList.size == 0)
    notifyItemRemoved(position)
  }

  override fun onItemMove(fromPosition: Int, toPosition: Int) {
    Collections.swap(habitList, fromPosition, toPosition)
    movableHabitList.onItemMove(fromPosition, toPosition)
//    val fromPositionId = habitList[fromPosition].id
//    habitList[fromPosition].id = habitList[toPosition].id
//    habitList[toPosition].id = fromPositionId
    notifyItemMoved(fromPosition, toPosition)
  }

  private class UpdateTask(val habitsDatabase: HabitsDatabase) : AsyncTask<Habit, Unit, Unit>() {
    override fun doInBackground(vararg habits: Habit) = habitsDatabase.habitDao().update(habits[0])
  }
}

