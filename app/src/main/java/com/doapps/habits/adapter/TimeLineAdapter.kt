package com.doapps.habits.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.doapps.habits.R
import com.doapps.habits.models.Habit
import com.doapps.habits.models.IListUpdater
import com.doapps.habits.view.holders.TimeLineViewHolder
import com.github.vipulasri.timelineview.TimelineView


class TimeLineAdapter(var feedList: List<Habit>) : RecyclerView.Adapter<TimeLineViewHolder>(), IListUpdater<Habit> {

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.habit_timeline_item, parent, false)
        return TimeLineViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {
        val habit = feedList[position]
        holder.textView.text = habit.title
    }

    override fun getItemCount(): Int {
        return feedList.size
    }

    override fun updateList(data: List<Habit>) {
        feedList = data
        notifyDataSetChanged()
    }
}