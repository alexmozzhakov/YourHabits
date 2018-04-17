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

/**
 * The receiver for an application notifications
 */
class NotificationReceiver : BroadcastReceiver() {

  /**
   * Handles notification and binds it to a habit id, before passing to service
   *
   * @param context application context
   * @param intent intent of a notification
   */
  override fun onReceive(context: Context, intent: Intent) {
    if (BuildConfig.DEBUG) Log.i(TAG, "received for id = ${intent.extras.getLong("id")}")
    val notificationIntent = Intent(context, NotificationIntentService::class.java)
    notificationIntent.putExtra("id", intent.extras.getLong("id"))
    context.startService(notificationIntent)
    // Notification is handled in MainActivity
  }

  /**
   * Sets timer for a notification to run
   *
   * @param context application context
   * @param id the id of an habit to build a notification for
   */
  fun setAlarm(context: Context, id: Long) {
    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java)
    intent.putExtra("id", id)
    if (BuildConfig.DEBUG) Log.i(TAG, "Setting an alarm for id = $id")

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
    /**
     * TAG is defined for logging errors and debugging information
     */
    private val TAG = NotificationReceiver::class.java.simpleName
  }
}

