package com.doapps.habits.view.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

import com.doapps.habits.R
import com.github.vipulasri.timelineview.TimelineView

class TimeLineViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {
    var textView: TextView = itemView.findViewById(R.id.habit_title)
    val mTimelineView: TimelineView = itemView.findViewById(R.id.time_marker)

    init {
        mTimelineView.initLine(viewType)
    }
}