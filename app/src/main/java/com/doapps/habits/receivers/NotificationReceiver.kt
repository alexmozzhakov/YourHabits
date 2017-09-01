package com.doapps.habits.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    Log.i("NotificationReceiver", "received for id = ${intent.extras.getLong("id")}")
    val notificationIntent = Intent(context, NotificationIntentService::class.java)
    notificationIntent.putExtra("id", intent.extras.getLong("id"))
    notificationIntent.putExtra("question", intent.extras.getString("question"))
    context.startService(notificationIntent)
    // Notification is handled in MainActivity
  }

  companion object {
    val TAG: String = NotificationReceiver::class.java.simpleName
  }

  fun setAlarm(context: Context, id: Long, question: String?) {
    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java)
    intent.putExtra("id", id)
    if (question == null)
      intent.putExtra("question", "Did you do something today?")
    else
      intent.putExtra("question", question)

    val pi = PendingIntent.getBroadcast(context, 0, intent, 0)

    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY) // 14 24-14 = 10 + 9 = 19:7
    val minute = calendar.get(Calendar.MINUTE) // 53 60-53 = 7 + 0 = 7
    val hourOff = 9
    val minuteOff = 0
    val startMs = TimeUnit.MINUTES.toMillis((60L + minuteOff - minute) % 60) +
        TimeUnit.HOURS.toMillis((24L + hourOff - hour) % 24)
    Log.i(TAG, "Starts in ${startMs / 1000} seconds")
    am.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + startMs, AlarmManager.INTERVAL_DAY, pi)
  }

  fun cancelAlarm(context: Context) {
    val intent = Intent(context, NotificationReceiver::class.java)
    val sender = PendingIntent.getBroadcast(context, 0, intent, 0)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(sender)
  }
}

