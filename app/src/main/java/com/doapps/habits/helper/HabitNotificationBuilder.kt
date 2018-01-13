package com.doapps.habits.helper

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import com.doapps.habits.R
import com.doapps.habits.activity.MainActivity

class HabitNotificationBuilder(val id: Int) : NotificationBuilder {
  @SuppressLint("NewApi")
  override fun create(context: Context, str: String): Notification {
    val mBuilder = NotificationCompat.Builder(context, "notify_001")
    val resultIntent = Intent(context, MainActivity::class.java)

    val taskStackBuilder = TaskStackBuilder.create(context)
    taskStackBuilder.addNextIntent(resultIntent)
    taskStackBuilder.addParentStack(MainActivity::class.java)

    val resultPendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

    mBuilder.setSmallIcon(R.drawable.ic_check_white_24dp)
    mBuilder.setContentTitle(context.getString(R.string.app_name))
    mBuilder.setContentText(str)

    // Setting up yesIntent
    val yesIntent = Intent(context, MainActivity::class.java).apply {
      action = "yes"
      putExtra("id", id)
    }
    val resultYesIntent = PendingIntent.getActivity(context, 0, yesIntent, PendingIntent.FLAG_CANCEL_CURRENT)

    // Setting up noIntent
    val noIntent = Intent(context, MainActivity::class.java).apply {
      action = "no"
      putExtra("id", id)
    }
    val resultNoIntent = PendingIntent.getActivity(context, 0, noIntent, PendingIntent.FLAG_CANCEL_CURRENT)

    mBuilder.apply {
      addAction(R.drawable.ic_check_white_24dp, "Yes", resultYesIntent)
      addAction(R.drawable.ic_no_black_24dp, "No", resultNoIntent)
    }
    mBuilder.setContentIntent(resultPendingIntent)
    return mBuilder.build()
  }
}

