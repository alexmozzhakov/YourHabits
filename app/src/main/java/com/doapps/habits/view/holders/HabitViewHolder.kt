package com.doapps.habits.view.holders

import android.animation.ValueAnimator
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import com.doapps.habits.R
import com.doapps.habits.models.Habit

class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
  val titleTextView: TextView = itemView.findViewById(R.id.habit_title)
  val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
  private val progressBar: ProgressBar = itemView.findViewById(R.id.circular_progress_bar)

  internal fun setProgressBar(habit: Habit) {
    if (habit.doneCounter != 0) {
      val progress = when (habit.frequency.size) {
        2 -> habit.doneCounter * 100 / habit.frequency[0]
        else -> {
          val daysCount = when (habit.frequency.last()) {
            0 -> 1
            else -> habit.frequency.size - 1
          }
          habit.doneCounter * 100 / daysCount
        }
      }
      animateProgress(progress)
    } else animateProgress(0)
  }

  private fun animateProgress(final: Int) {
    val animator = ValueAnimator.ofInt(progressBar.progress, final)
    animator.addUpdateListener {
      val progress = it.animatedValue as Int
      progressBar.progress = progress
    }
    animator.interpolator = DecelerateInterpolator()
    animator.duration = 400
    animator.start()
  }
}