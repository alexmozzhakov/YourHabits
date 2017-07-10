package com.doapps.habits.helper

import android.util.Log
import com.doapps.habits.models.Habit
import java.util.*

internal object DayFilter {

  @JvmStatic
  fun filterListForToday(todayHabits: MutableCollection<Habit>) {
    val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    val habitIterator = todayHabits.iterator()
    while (habitIterator.hasNext()) {
      val freq = habitIterator.next().frequencyArray
      if (freq.isEmpty()) {
        Log.w("filterListByDay()", "frequency not set")
      } else if (freq.size > 2 && notToday(freq, dayOfWeek)) habitIterator.remove()
    }
  }

  @JvmStatic
  fun filterListByDay(dayHabits: MutableCollection<Habit>, dayOfWeek: Int) {
    val habitIterator: MutableIterator<Habit> = dayHabits.iterator()
    while (habitIterator.hasNext()) {
      val freq = habitIterator.next().frequencyArray
      if (freq.isEmpty()) {
        Log.w("filterListByDay()", "frequency not set")
      } else if (!isEveryday(freq)) {
        if (once(freq) || notToday(freq, dayOfWeek)) habitIterator.remove()
      }
    }
  }

  fun once(freq: IntArray) = freq.size <= 2
  fun notToday(freq: IntArray, dayOfWeek: Int) = (1 until freq.size).none { freq[it] == dayOfWeek }
  fun isEveryday(freq: IntArray) = freq.size == 2 && freq[0] == freq[1]
}
