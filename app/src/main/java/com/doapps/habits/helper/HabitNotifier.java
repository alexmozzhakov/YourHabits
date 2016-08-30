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
    private final Context context;
    private final String mQuestion;

    public HabitNotifier(Context context, String question) {
        this.context = context;
        mQuestion = question;
    }

    public void initiate() {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm.class);
        intent.putExtra("message", mQuestion);
        intent.putExtra("title", "doHabit");
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setContentText(intent.getStringExtra("message"))
                .setContentTitle("doHabits")
                .setColor(Color.parseColor("#2CACE1"))
                .setSmallIcon(R.drawable.ic_check_white_24dp)
                .setAutoCancel(true)
//                .addAction(R.drawable.ic_check_black_24dp, "Yes", yesIntent)
//                .addAction(R.drawable.ic_no_black_24dp, "No", noIntent)
                .setContentIntent(resultPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_SOUND);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification.build());
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        2000, alarmIntent);
    }
}
