package com.doapps.habits.adapter

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.doapps.habits.R
import com.doapps.habits.dao.HabitDao
import com.doapps.habits.database.HabitsDatabase
import com.doapps.habits.models.Habit
import com.doapps.habits.view.holders.HabitViewHolder
import java.util.*

class HabitSearchAdapter(private val habitList: List<Habit>, private val habitsDatabase: HabitDao)
    : RecyclerView.Adapter<HabitViewHolder>(), IMovableListAdapter {

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        //ignored
    }

    override fun onItemDismiss(position: Int) {
        //ignored
    }

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
                if (HabitsDatabase.mustHaveFollowed(habit))
                    habit.followingFrom = System.currentTimeMillis()
                habitsDatabase.update(habit)
                holder.titleTextView.setTextColor(Color.GRAY)
            } else {
                habit.isDoneMarker = false
                habitsDatabase.update(habit)
                holder.titleTextView.setTextColor(Color.BLACK)
            }
        }

        val calendar = Calendar.getInstance()

        holder.checkBox.isChecked = habit.isDone(
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR))

    }

    override fun getItemCount(): Int {
        return habitList.size
    }
}