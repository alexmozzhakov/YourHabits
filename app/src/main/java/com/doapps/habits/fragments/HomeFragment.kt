package com.doapps.habits.fragments

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.doapps.habits.BuildConfig
import com.doapps.habits.R
import com.doapps.habits.activity.MainActivity
import com.doapps.habits.adapter.TimeLineAdapter
import com.doapps.habits.database.HabitsDatabase
import com.doapps.habits.helper.ConnectionManager
import com.doapps.habits.helper.ConnectionReceiver
import com.doapps.habits.helper.HabitListManager
import com.doapps.habits.helper.HomeDayManager
import com.doapps.habits.listeners.WeatherNetworkStateListener
import com.doapps.habits.models.*
import com.doapps.habits.slider.swipeselector.SwipeStringSelector
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class HomeFragment : Fragment(), IWeatherUpdater {
  private lateinit var weather: TextView
  private lateinit var weatherBot: TextView
  private var habitListSize: Int = 0
  private var connectionReceiver: ConnectionReceiver? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    val view = inflater.inflate(R.layout.fragment_home, container, false)

    val toolbar = (activity as MainActivity).toolbar
    toolbar.setTitle(R.string.home)

    weather = view.findViewById(R.id.weather)
    weatherBot = view.findViewById(R.id.weatherBot)

    habitsDatabase = HabitListManager.getInstance(context).database
    GetTask(view).execute()

    if (ConnectionManager.isConnected(activity!!)) {
      getWeather()
    } else {
      weather.text = habitListSize.toString()
      weatherBot.setText(R.string.all_tasks)

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val conMan = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeListener = object : ConnectivityManager.OnNetworkActiveListener {
          @TargetApi(Build.VERSION_CODES.LOLLIPOP)
          override fun onNetworkActive() {
            if (BuildConfig.DEBUG) Log.i("HF", "onNetworkActive")
            getWeather()

            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
              FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener {
                val ac = activity as MainActivity?
                ac!!.onSetupNavigationDrawer(
                    FirebaseAuth.getInstance().currentUser)
              }
            } else {
              (activity as MainActivity).onSetupNavigationDrawer(user)
            }
            conMan.removeDefaultNetworkActiveListener(this)
          }

        }
        conMan.addDefaultNetworkActiveListener(activeListener)
        if (BuildConfig.DEBUG) Log.i(TAG, "Added default network active listener")
      } else {
        val weatherNetworkStateListener = WeatherNetworkStateListener(this, (activity as MainActivity?)!!)
        connectionReceiver = ConnectionReceiver(weatherNetworkStateListener)

        val intentFilter = IntentFilter(BROADCAST)
        activity!!.registerReceiver(connectionReceiver, intentFilter)
        if (BuildConfig.DEBUG) Log.i(TAG, "Added default network active listener")
      }
    }
    return view
  }

  private fun getDaysOfWeekFromToday(dayOfWeek: Int): Array<String> {
    val daysOfWeekNames = activity!!.resources.getStringArray(R.array.days_of_week_array)
    return Array(7, { i -> daysOfWeekNames[(dayOfWeek + i) % 7] })
  }

  @SuppressLint("SetTextI18n")
  override fun getWeather() {
    Volley.newRequestQueue(context!!.applicationContext).add(
        StringRequest(Request.Method.GET, URL_WEATHER_API,
            { response ->
              try {
                val jsonResponse = JSONObject(response)

                if (jsonResponse.has("error")) {
                  onNetworkFail(
                      Exception(jsonResponse.getString("error")), habitListSize)
                } else {
                  if (activity != null) {
                    activity!!
                        .getSharedPreferences("pref", Context.MODE_PRIVATE)
                        .edit()
                        .putString("location", jsonResponse.getString("location"))
                        .apply()
                  }

                  weather.text = jsonResponse.getString("celsius") + "˚C"
                  weatherBot.text = jsonResponse.getString("location")
                }

              } catch (error: JSONException) {
                onNetworkFail(error, habitListSize)
              }

            }
        ) { error -> onNetworkFail(error, habitListSize) }
    )
  }

  private fun onNetworkFail(error: Exception, listSize: Int) {
    if (activity == null) {
      return
    }
    Log.e("StringRequest error", error.toString())
    weather.text = listSize.toString()
    weatherBot.setText(R.string.all_tasks)
    (activity as MainActivity).removeAvatarPadding()
  }

  override fun onDestroy() {
    if (connectionReceiver != null) {
      activity!!.unregisterReceiver(connectionReceiver)
    }
    super.onDestroy()
  }

  @SuppressLint("StaticFieldLeak")
  private inner class GetTask internal constructor(private val view: View) : AsyncTask<Void, Void, List<Habit>>() {

    override fun doInBackground(vararg voids: Void): List<Habit> {
      return habitsDatabase!!.habitDao().all
    }

    override fun onPostExecute(habits: List<Habit>) {
      super.onPostExecute(habits)

      // set filtered list to adapter
      val timeLineAdapter = TimeLineAdapter(habits)
      timeLineAdapter.setHasStableIds(true)
      val recyclerView = view.findViewById<RecyclerView>(R.id.timeline)
      recyclerView.layoutManager = LinearLayoutManager(activity)
      recyclerView.adapter = timeLineAdapter

      // filter list for today
      val dayManager = HomeDayManager(timeLineAdapter, habits)
      dayManager.updateForToday()

      // set due count of filtered list
      val tasksDue = view.findViewById<TextView>(R.id.tasks_due_num)
      tasksDue.text = dayManager.getDueCount()

      // get list size
      habitListSize = habits.size

      // get day of week
      val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

      val swipeSelector: SwipeStringSelector = view.findViewById<View>(R.id.sliding_tabs) as SwipeStringSelector
      swipeSelector.setItems(*getDaysOfWeekFromToday(dayOfWeek - 1))

      initDaysTabs(
          dayOfWeek,
          swipeSelector,
          HomeDayManager(timeLineAdapter, habits))

    }
  }

  companion object {

    private val URL_WEATHER_API = "http://habit.esy.es/weather.php"
    private val BROADCAST = "android.net.conn.CONNECTIVITY_CHANGE"
    private val TAG = HomeFragment::class.java.simpleName
    private var habitsDatabase: HabitsDatabase? = null

    /**
     * Initiates
     *
     * @param swipeStringSelector by using
     * @param habitDayManager for filtering list
     * @param dayOfWeek for getting dayOfWeek from number of selected item
     */
    private fun initDaysTabs(
        dayOfWeek: Int,
        swipeStringSelector: IStringSelector,
        habitDayManager: IDayManager) {
      swipeStringSelector.setOnItemSelectedListener(object : IOnItemSelectedListener<String>() {
        override fun onItemSelected(item: String) {
          val value = swipeStringSelector.adapter.currentPosition
          if (value == 0) {
            habitDayManager.updateForToday()

            if (BuildConfig.DEBUG) Log.i(TAG, "Selected day (today) = " + dayOfWeek)

          } else {
            val daysFromWeekStart = dayOfWeek + value
            val day = if (daysFromWeekStart > 7) daysFromWeekStart % 7 else daysFromWeekStart

            if (BuildConfig.DEBUG) Log.i(TAG, "Selected day = " + day)
            habitDayManager.updateListByDayOfWeek(day)
          }
        }
      })
    }
  }
}