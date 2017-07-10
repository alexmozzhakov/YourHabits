package com.doapps.habits.helper

import com.doapps.habits.models.Habit
import com.doapps.habits.models.IDayManager
import com.doapps.habits.models.IListUpdater
import java.util.*

class HomeDayManager(private val mTimeLineAdapter: IListUpdater<Habit>,
                     private val mHabitList: List<Habit>) : IDayManager, DueCounter {
  private var dueCount: Int = 0

  override fun updateListByDayOfWeek(dayOfWeek: Int) {
    val dayHabits = ArrayList(mHabitList)
    DayFilter.filterListByDay(dayHabits, dayOfWeek)
    dueCount = countDue(dayHabits)
    mTimeLineAdapter.updateList(dayHabits)
  }

  override fun updateForToday() {
    val todayHabits = ArrayList(mHabitList)
    DayFilter.filterListForToday(todayHabits)
    dueCount = countDue(todayHabits)
    mTimeLineAdapter.updateList(todayHabits)
  }

  private fun countDue(list: List<Habit>): Int {
    val calendar = Calendar.getInstance()
    val date = calendar.get(Calendar.DATE)
    val month = calendar.get(Calendar.MONTH)
    val year = calendar.get(Calendar.YEAR)
    return list.count { !it.isDone(date, month, year) }
  }

  override fun getDueCount() = dueCount.toString()
}