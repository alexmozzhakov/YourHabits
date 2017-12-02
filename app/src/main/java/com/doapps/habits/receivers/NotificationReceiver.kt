package com.doapps.habits.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.doapps.habits.BuildConfig
import com.doapps.habits.services.NotificationIntentService
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    if (BuildConfig.DEBUG)
      Log.i("NotificationReceiver", "received for id = ${intent.extras.getLong("id")}")
    val notificationIntent = Intent(context, NotificationIntentService::class.java)
    notificationIntent.putExtra("id", intent.extras.getLong("id"))
    notificationIntent.putExtra("question", intent.extras.getString("question"))
    context.startService(notificationIntent)
    // Notification is handled in MainActivity
  }

  fun setAlarm(context: Context, id: Long, question: String?) {
    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java)
    intent.putExtra("id", id)
    Log.i(TAG, "GOT setAlarm id = $id")
    intent.putExtra("question", question)

    val pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val hourOff = if (BuildConfig.DEBUG) 10 else 9
    val minuteOff = if (BuildConfig.DEBUG) 50 else 0
    val startMs = TimeUnit.MINUTES.toMillis((60L + minuteOff - minute) % 60) +
        TimeUnit.HOURS.toMillis((24L + hourOff - hour) % 24)
    if (BuildConfig.DEBUG) Log.i(TAG, "Starts in ${startMs / 1000} seconds")
    am.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + startMs, AlarmManager.INTERVAL_DAY, pi)
  }

  companion object {
    val TAG: String = NotificationReceiver::class.java.simpleName

    fun cancelAlarm(context: Context, habitId: Int) {
      val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
      val myIntent = Intent(context, NotificationIntentService::class.java)
      val pendingIntent = PendingIntent.getActivity(context, habitId, myIntent, PendingIntent.FLAG_UPDATE_CURRENT)
      pendingIntent.cancel()
      am.cancel(pendingIntent)
    }
  }
}

