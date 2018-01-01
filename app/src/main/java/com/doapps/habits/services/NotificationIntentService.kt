package com.doapps.habits.services

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.doapps.habits.BuildConfig
import com.doapps.habits.helper.HabitListManager
import com.doapps.habits.helper.HabitNotificationBuilder
import com.doapps.habits.helper.NotificationManagerFactory


class NotificationIntentService : IntentService("NotificationIntentService") {
  override fun onHandleIntent(intent: Intent) {
    val id = intent.extras.getLong("id", 0)
    if (BuildConfig.DEBUG) Log.i(TAG, "Passed id = $id")
    val habit = HabitListManager.getInstance(applicationContext)[id]
    if (habit != null) {
      if (BuildConfig.DEBUG) Log.i(TAG, "Passed question = ${habit.question}")
      val notificationManager = NotificationManagerFactory().create(applicationContext, "Reminders channel")
      val notification = HabitNotificationBuilder(id.toInt()).create(applicationContext, habit.question)
      notificationManager.notify(id.toInt(), notification)
    }
  }

  companion object {
    /**
     * TAG is defined for logging errors and debugging information
     */
    private val TAG = NotificationIntentService::class.java.simpleName
  }
}