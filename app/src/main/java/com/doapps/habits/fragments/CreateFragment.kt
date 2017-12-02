package com.doapps.habits.fragments

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.doapps.habits.BuildConfig
import com.doapps.habits.R
import com.doapps.habits.activity.MainActivity
import com.doapps.habits.database.HabitsDatabase
import com.doapps.habits.helper.HabitListManager
import com.doapps.habits.models.Habit
import com.doapps.habits.receivers.NotificationReceiver
import java.util.*

class CreateFragment : Fragment(), AdapterView.OnItemSelectedListener {

  private lateinit var editTime: TextInputEditText
  private lateinit var editQuestion: TextInputEditText
  private lateinit var editTitle: TextInputEditText
  private lateinit var sFrequency: Spinner
  private lateinit var tvFreqNum: TextView
  private lateinit var tvFreqDen: TextView
  private lateinit var llCustomFrequency: ViewGroup
  private var lastSpinnerSelection: Int = 0
  private var habitsDatabase: HabitsDatabase? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    val result = inflater.inflate(R.layout.fragment_create, container, false)
    editTitle = result.findViewById(R.id.edit_title)
    editQuestion = result.findViewById(R.id.edit_question)
    editTitle.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        editTitle.error = "Can't be empty"
      }

      override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        if (charSequence.isNotEmpty()) {
          val start = Character.toLowerCase(charSequence[0])
          editQuestion.setText(String.format("Did you %s%s today?", start,
              charSequence.subSequence(1, charSequence.length)))
        }
      }

      override fun afterTextChanged(editable: Editable) {
        if (editable.isEmpty()) {
          editTitle.error = "Can't be empty"
        }
      }
    })
    editTime = result.findViewById(R.id.edit_time)
    sFrequency = result.findViewById(R.id.sFrequency)
    tvFreqNum = result.findViewById(R.id.input_freq_num)
    tvFreqDen = result.findViewById(R.id.input_freq_den)
    llCustomFrequency = result.findViewById(R.id.llCustomFrequency)

    sFrequency.onItemSelectedListener = this
    result.findViewById<View>(R.id.btnCreate).setOnClickListener { onHabitCreate() }
    (activity as MainActivity).toolbar.setTitle(R.string.habit_create)

    return result
  }

  private fun onHabitCreate() {
    if (editTitle.length() != 0) {
      val frequency = when (lastSpinnerSelection) {
        0 -> intArrayOf(1, 1)
        1 -> intArrayOf(1, 7)
        2 -> intArrayOf(2, 7)
        3 -> intArrayOf(5, 7)
        else -> intArrayOf(Integer.valueOf(tvFreqNum.text.toString())!!, Integer.valueOf(tvFreqDen.text.toString())!!)
      }

      if (BuildConfig.DEBUG) {
        Log.i("onHabitCreate", "freq = " + Arrays.toString(frequency))
      }

      val time = when {
        editTime.text.toString().isNotEmpty() -> Integer.valueOf(editTime.text.toString())
        else -> 0
      }
      // Checks what data was entered and adds habit to habitsDatabase
      habitsDatabase = HabitListManager.getInstance(context).habitsDatabase
      val upd = Calendar.getInstance()
      val habit = Habit(
          editTitle.text.toString(),
          editQuestion.text.toString(),
          false,
          upd.get(Calendar.DATE),
          upd.get(Calendar.MONTH),
          upd.get(Calendar.YEAR),
          time,
          System.currentTimeMillis(),
          0, *frequency)
      InsertHabitTask().execute(habit)
    } else {
      Toast.makeText(context!!.applicationContext, "Please enter habit's title", Toast.LENGTH_SHORT).show()
    }
  }

  override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
    if (view != null) {
      lastSpinnerSelection = i
      if (i == 4) {
        sFrequency.visibility = View.GONE
        llCustomFrequency.visibility = View.VISIBLE
      }
    }
//    else {
//      sFrequency.visibility = View.VISIBLE
//      llCustomFrequency.visibility = View.GONE
//    }
  }

  override fun onNothingSelected(adapterView: AdapterView<*>) {
    // ignored
  }

  override fun onPause() {
    (activity as MainActivity).closeImm()
    super.onPause()
  }

  @SuppressLint("StaticFieldLeak")
  private inner class InsertHabitTask : AsyncTask<Habit, Unit, Long>() {

    override fun doInBackground(vararg habit: Habit): Long? {
      if (BuildConfig.DEBUG) {
        Log.i("InsertHabitTask", "New program added")
      }
      return habitsDatabase!!.habitDao().insert(habit[0])
    }

    override fun onPostExecute(id: Long?) {
      val notificationService = NotificationReceiver()
      notificationService.setAlarm(context!!, id!!, editQuestion.text.toString())
      (activity as MainActivity).toolbar.title = getString(R.string.list)
      (activity as MainActivity).setChecked(2)
      (activity as MainActivity).mLastFragment = R.id.nav_list

      fragmentManager!!.beginTransaction().replace(R.id.content_frame, ListFragment())
          .commit()
      super.onPostExecute(id)
    }
  }
}
