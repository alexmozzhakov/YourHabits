package com.doapps.habits.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.doapps.habits.receivers.Alarm;

public class NotificationService extends Service {
    private final Alarm alarm = new Alarm();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarm.setAlarm(this);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}