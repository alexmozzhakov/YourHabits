package com.doapps.habits

import android.app.Application

// TODO: implement Notification logic
class App : Application() {
  override fun onCreate() {
    super.onCreate()
//    val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
//
//    val myJob = dispatcher.newJobBuilder()
//        .setService(MyJobService::class.java) // the JobService that will be called
//        .setTag("my-unique-tag")        // uniquely identifies the job
//        .build()
//
//    dispatcher.mustSchedule(myJob)
  }

//  fun scheduleJob(freq: IntArray) {
//        if (DayFilter.isEveryday(freq)) {
//    Log.i(TAG, "1")
//    val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
//            val calendar = Calendar.getInstance()
//            val hour = calendar.get(Calendar.HOUR_OF_DAY) // 19
//            val minute = calendar.get(Calendar.MINUTE) // 18
//            val minuteOff = 49
//            val hourOff = 19
//
//            // 8 AM - 9 AM, ignore seconds
//            val startMs = TimeUnit.MINUTES.toSeconds((60L + minuteOff - 1 - minute)) +
//                    TimeUnit.HOURS.toSeconds(((24L + hourOff - 1 - hour) % 24))
//            val endMs = startMs + TimeUnit.HOURS.toMillis(1)

//            val myJob = dispatcher.newJobBuilder()
//                    .setService(MyJobService::class.java)
//                    .setTag("my-unique-tag")
//                    .setRecurring(true)
//                    .setLifetime(Lifetime.FOREVER)
//                    .setTrigger(Trigger.executionWindow(0, 15))
//                    .setReplaceCurrent(false)
//                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
//                    .build()
//    val myJob2 = dispatcher.newJobBuilder()
//        .setService(MyJobService::class.java) // the JobService that will be called
//        .setTag("my-unique-tag")        // uniquely identifies the job
//        .build()

//            dispatcher.mustSchedule(myJob)
//    dispatcher.mustSchedule(myJob2)
//        } else if (DayFilter.once(freq)) {
//
//        } else {
//
//        }
//  }

  companion object {
    val TAG = "EverydayJob"
  }
}