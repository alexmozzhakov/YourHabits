package com.doapps.habits.helper

import android.util.Log
import com.doapps.habits.BuildConfig
import com.doapps.habits.models.Habit
import java.util.*

object DayFilter {

  @JvmStatic
  fun filterListForToday(todayHabits: MutableCollection<Habit>) {
    val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    val habitIterator = todayHabits.iterator()
    while (habitIterator.hasNext()) {
      val freq = habitIterator.next().frequencyArray

      if (!freq.isEmpty() && freq.size > 2 && notToday(freq, dayOfWeek)) habitIterator.remove()
      else if (BuildConfig.DEBUG) Log.w("filterListByDay()", "frequency not set")
    }
  }

  @JvmStatic
  fun filterListByDay(dayHabits: MutableCollection<Habit>, dayOfWeek: Int) {
    val habitIterator: MutableIterator<Habit> = dayHabits.iterator()
    while (habitIterator.hasNext()) {
      val freq = habitIterator.next().frequencyArray

      if (!freq.isEmpty() && !isEveryday(freq) && (once(freq) || notToday(freq, dayOfWeek))) habitIterator.remove()
      else if (BuildConfig.DEBUG) Log.w("filterListByDay()", "frequency not set")
    }
  }

  private fun once(freq: IntArray) = freq.size <= 2
  private fun notToday(freq: IntArray, dayOfWeek: Int) = (1 until freq.size).none { freq[it] == dayOfWeek }
  private fun isEveryday(freq: IntArray) = freq.size == 2 && freq[0] == freq[1]
}
