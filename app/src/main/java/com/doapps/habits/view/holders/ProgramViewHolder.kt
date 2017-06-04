package com.doapps.habits.view.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

import com.doapps.habits.R

class ProgramViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleTextView: TextView = itemView.findViewById(R.id.program_title)
    val percentTextView: TextView = itemView.findViewById(R.id.program_percent)
}
