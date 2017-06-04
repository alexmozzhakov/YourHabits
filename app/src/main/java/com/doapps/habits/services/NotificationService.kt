package com.doapps.habits.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

import com.doapps.habits.receivers.Alarm

class NotificationService : Service() {
    private val alarm = Alarm()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        alarm.setAlarm(this)
        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}