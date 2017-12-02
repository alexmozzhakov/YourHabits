package com.doapps.habits.helper

import android.annotation.SuppressLint
import android.arch.persistence.room.Room
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.doapps.habits.database.HabitsDatabase
import com.doapps.habits.database.ProgramsDatabase
import com.doapps.habits.models.Habit
import com.doapps.habits.receivers.NotificationReceiver
import java.util.concurrent.ExecutionException

class HabitListManager private constructor(private val context: Context) {

  val list: MutableList<Habit>
    @Throws(InterruptedException::class, ExecutionException::class)
    get() = GetTask().execute().get()

  var habitsDatabase: HabitsDatabase = Room.databaseBuilder(context, HabitsDatabase::class.java, "habits").build()
  var programDatabase = Room.databaseBuilder(context, ProgramsDatabase::class.java, "programs").build()

  @Throws(InterruptedException::class, ExecutionException::class)
  operator fun get(id: Long): Habit = GetHabitTask().execute(id).get()

  @Throws(InterruptedException::class, ExecutionException::class)
  fun update(habit: Habit) {
    UpdateTask().execute(habit)
  }

  fun onItemDismiss(position: Int) {
    val habitId = DeleteTask().execute(position).get()
    Log.i("ListManager", "habitId = $habitId")
    NotificationReceiver.cancelAlarm(context, habitId)
  }

  fun onItemMove(fromPosition: Habit, toPosition: Habit) {
    MoveTask().execute(fromPosition, toPosition)
  }

  private class DeleteTask : AsyncTask<Int, Unit, Int>() {
    override fun doInBackground(vararg params: Int?): Int {
      val habits = HabitListManager.instance!!.habitsDatabase.habitDao().all
      val habit = habits[params[0]!!]
      if (habit.isProgram) {
        HabitListManager.instance!!.programDatabase.programDao().delete(habit.id)
      }
      HabitListManager.instance!!.habitsDatabase.habitDao().delete(habit)
      return habit.id
    }
  }

  private class MoveTask : AsyncTask<Habit, Unit, Unit>() {

    override fun doInBackground(vararg habits: Habit) {
      HabitListManager.instance!!.habitsDatabase.habitDao().update(habits[0])
      HabitListManager.instance!!.habitsDatabase.habitDao().update(habits[1])
    }
  }

  private class GetTask : AsyncTask<Unit, Unit, MutableList<Habit>>() {
    override fun doInBackground(vararg voids: Unit): MutableList<Habit> = HabitListManager.instance!!.habitsDatabase.habitDao().all
  }

  private class GetHabitTask : AsyncTask<Long, Unit, Habit>() {
    override fun doInBackground(vararg params: Long?): Habit? = HabitListManager.instance!!.habitsDatabase.habitDao().get(params[0]!!)
  }


  @SuppressLint("StaticFieldLeak")
  private inner class UpdateTask : AsyncTask<Habit, Unit, Unit>() {
    override fun doInBackground(habits: Array<Habit>) = habitsDatabase.habitDao().update(habits[0])
  }

  companion object {
    @Volatile private var instance: HabitListManager? = null

    fun getInstance(context: Context?): HabitListManager {
      if (instance == null) {
        synchronized(HabitListManager::class.java) {
          if (instance == null && context != null) {
            instance = HabitListManager(context.applicationContext)
          }
        }
      }
      return instance!!
    }
  }
}
