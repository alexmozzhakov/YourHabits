package com.doapps.habits.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.doapps.habits.BuildConfig
import com.doapps.habits.R
import com.doapps.habits.converters.IntegerArrayConverter
import com.doapps.habits.database.HabitsDatabase
import com.doapps.habits.database.ProgramsDatabase
import com.doapps.habits.helper.HabitListManager
import com.doapps.habits.models.Achievement
import com.doapps.habits.models.Habit
import com.doapps.habits.models.Program
import com.doapps.habits.receivers.NotificationReceiver
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import java.util.concurrent.ExecutionException

class ProgramFragment : Fragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    val result = inflater.inflate(R.layout.fragment_program, container, false)

    val fab = result.findViewById<FloatingActionButton>(R.id.fab)
    val description = result.findViewById<TextView>(R.id.description)
    val habitListManager = HabitListManager.getInstance(context)
    programDatabase = habitListManager.programDatabase
    habitDatabase = habitListManager.habitsDatabase

    val position = arguments!!.getInt("pos", 0)
    val programRef = FirebaseDatabase.getInstance().reference.child("programs")
        .child(Integer.toString(position))
    programRef.child("habit").child("description").addValueEventListener(object : ValueEventListener {
      override fun onDataChange(dataSnapshot: DataSnapshot) {
        description.text = dataSnapshot.getValue(String::class.java)
      }

      override fun onCancelled(databaseError: DatabaseError) {
        Log.e("onCancelled", databaseError.message)
      }
    })
    programRef.addValueEventListener(object : ValueEventListener {
      override fun onDataChange(dataSnapshot: DataSnapshot) {
        if (BuildConfig.DEBUG) {
          Log.i("Program fragment", dataSnapshot.toString())
        }

        fab.setOnClickListener {
          try {
            val isInserted = InsertIfNotExists(dataSnapshot, context!!).execute().get()
            if (!isInserted) {
              Toast.makeText(context!!.applicationContext, "Already added",
                  Toast.LENGTH_SHORT).show()
            }
          } catch (e: InterruptedException) {
            e.printStackTrace()
          } catch (e: ExecutionException) {
            e.printStackTrace()
          }
        }
      }

      override fun onCancelled(databaseError: DatabaseError) {
        Log.e("onCancelled", databaseError.message)
        activity!!.supportFragmentManager.beginTransaction()
            .remove(this@ProgramFragment)
            .commit()
      }
    })
    return result
  }

  private class InsertTask : AsyncTask<Program, Unit, Unit>() {

    override fun doInBackground(vararg programs: Program) {
      Log.i("DatabaseContains", "New program added")
      programDatabase.programDao().insertAll(*programs)
      DatabasePrintTask().execute()
    }
  }

  @SuppressLint("StaticFieldLeak")
  private class InsertIfNotExists internal constructor(private val mSnapshot: DataSnapshot, private val mContext: Context) : AsyncTask<Unit, Unit, Boolean>() {

    override fun doInBackground(vararg voids: Unit): Boolean? {
      val exists = programDatabase.programDao().idExists(Integer.parseInt(mSnapshot.key)) == 0
      if (exists) {
        InsertHabitTask(mSnapshot, mContext).execute()
      }
      return exists
    }
  }

  private class DatabasePrintTask : AsyncTask<Unit, Unit, Unit>() {

    override fun doInBackground(vararg voids: Unit) {
      Log.i("New Database", Arrays.toString(programDatabase.programDao().all.toTypedArray()))
    }
  }

  @SuppressLint("StaticFieldLeak")
  private class InsertHabitTask internal constructor(private val dataSnapshot: DataSnapshot, private val context: Context) : AsyncTask<Unit, Unit, Long>() {

    override fun doInBackground(vararg voids: Unit): Long? {
      Log.i("DatabaseContains", "New program added")
      val upd = Calendar.getInstance()
      val habit = Habit(
          dataSnapshot.child("habit").child("title").getValue(String::class.java),
          dataSnapshot.child("habit").child("question").getValue(String::class.java),
          false,
          upd.get(Calendar.DATE),
          upd.get(Calendar.MONTH),
          upd.get(Calendar.YEAR),
          dataSnapshot.child("habit").child("time").getValue(Int::class.java)!!,
          System.currentTimeMillis(),
          dataSnapshot.child("habit").child("cost").getValue(Int::class.java)!!,
          *IntegerArrayConverter
              .toArray(dataSnapshot.child("habit").child("frequency").getValue(String::class.java)))
      return habitDatabase.habitDao().insert(habit)
    }

    override fun onPostExecute(id: Long?) {
      super.onPostExecute(id)
      val notificationService = NotificationReceiver()
      notificationService.setAlarm(context, id!!,
          dataSnapshot.child("habit").child("question").getValue(String::class.java))

      val achievements = createAchievementList(dataSnapshot.child("achievements"))

      Log.i("ID", dataSnapshot.key)
      val program = Program(
          Integer.valueOf(dataSnapshot.key)!!,
          dataSnapshot.child("name").getValue(String::class.java),
          dataSnapshot.child("success").getValue(String::class.java),
          id,
          achievements
      )
      InsertTask().execute(program)
    }
  }

  companion object {

    private lateinit var programDatabase: ProgramsDatabase
    private lateinit var habitDatabase: HabitsDatabase


    private fun createAchievementList(dataSnapshot: DataSnapshot): List<Achievement> {
      val achievements = ArrayList<Achievement>(dataSnapshot.childrenCount.toInt())
      for (achievementSnapshot in dataSnapshot.children) {
        val templates = Array(achievementSnapshot.childrenCount.toInt(), { "" })
        templates.map {
          achievementSnapshot.child("templates").children.forEach {
            it.child("name").getValue(String::class.java)
          }
        }
        val achievement = Achievement(
            achievementSnapshot.child("rating").getValue(Int::class.java),
            templates)
        achievements.add(achievement)
      }
      return achievements
    }
  }
}
