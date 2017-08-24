package com.doapps.habits.view.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.doapps.habits.R
import com.doapps.habits.views.CircularProgressBar

class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
  val titleTextView: TextView = itemView.findViewById(R.id.habit_title)
  val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
  val progressBar: CircularProgressBar = itemView.findViewById(R.id.circular_progress_bar)
}