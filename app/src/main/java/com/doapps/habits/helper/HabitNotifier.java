package com.doapps.habits.helper;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.doapps.habits.R;
import com.doapps.habits.activity.MainActivity;
import com.doapps.habits.receivers.Alarm;

public class HabitNotifier {
    private final Context mContext;
    private final String mQuestion;
    private final long mId;

    public HabitNotifier(Context context, String question, long id) {
        mContext = context;
        mQuestion = question;
        mId = id;
    }

    public void initiate() {
        AlarmManager alarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        // Setting up pendingIntent
        Intent intent = new Intent(mContext, Alarm.class);
        intent.putExtra("id", mId);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        Intent resultIntent = new Intent(mContext, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Setting up yesIntent
        Intent yesIntent = new Intent(mContext, MainActivity.class);
        yesIntent.setAction("yes");
        yesIntent.putExtra("id", mId);
        PendingIntent resultYesIntent =
                PendingIntent.getActivity(
                        mContext, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Setting up noIntent
        Intent noIntent = new Intent(mContext, MainActivity.class);
        noIntent.setAction("no");
        noIntent.putExtra("id", mId);
        PendingIntent resultNoIntent =
                PendingIntent.getActivity(
                        mContext, 0, noIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Building notification
        NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext)
                .setContentText(mQuestion)
                .setContentTitle("doHabits")
                .setColor(Color.parseColor("#2CACE1"))
                .setSmallIcon(R.drawable.ic_check_white_24dp)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_check_white_24dp, "Yes", resultYesIntent)
                .addAction(R.drawable.ic_no_black_24dp, "No", resultNoIntent)
                .setContentIntent(resultPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_SOUND);

        NotificationManager notificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification.build());
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        2000, alarmIntent);
    }
}
