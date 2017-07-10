package com.doapps.habits;

import android.util.Log;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class MyJobService extends JobService {

  @Override
  public boolean onStartJob(JobParameters job) {
    // Do some work here
    Log.i("Job", "start");
    return false; // Answers the question: "Is there still work going on?"
  }

  @Override
  public boolean onStopJob(JobParameters job) {
    return false; // Answers the question: "Should this job be retried?"
  }
}
