package com.doapps.habits.receivers

import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.doapps.habits.R
import com.doapps.habits.activity.MainActivity

class NotificationIntentService : IntentService("NotificationIntentService") {
  override fun onHandleIntent(intent: Intent) {
    val id = intent.extras.getLong("id", 0)
    Log.i("NotificationService", "Passed id = $id")
    val question = intent.extras.getString("question", "")
    Log.i("NotificationService", "Passed question = $question")
    val resultIntent = Intent(this, MainActivity::class.java)
    val resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT)

    // Setting up yesIntent
    val yesIntent = Intent(this, MainActivity::class.java)
    yesIntent.action = "yes"
    yesIntent.putExtra("id", id)
    val resultYesIntent = PendingIntent.getActivity(this, 0, yesIntent, PendingIntent.FLAG_CANCEL_CURRENT)

    // Setting up noIntent
    val noIntent = Intent(this, MainActivity::class.java)
    noIntent.action = "no"
    noIntent.putExtra("id", id)
    val resultNoIntent = PendingIntent.getActivity(this, 0, noIntent, PendingIntent.FLAG_CANCEL_CURRENT)

    // Building notification
    val notification = NotificationCompat.Builder(this, "habits")
        .setContentText(question)
        .setContentTitle("doHabits")
        .setColor(Color.parseColor("#2CACE1"))
        .setSmallIcon(R.drawable.ic_check_white_24dp)
        .setAutoCancel(true)
        .addAction(R.drawable.ic_check_white_24dp, "Yes", resultYesIntent)
        .addAction(R.drawable.ic_no_black_24dp, "No", resultNoIntent)
        .setContentIntent(resultPendingIntent)
        .setDefaults(NotificationCompat.DEFAULT_SOUND)

    val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(id.toInt(), notification.build())
  }
}