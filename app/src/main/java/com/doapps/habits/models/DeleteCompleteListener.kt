package com.doapps.habits.models

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser

class DeleteCompleteListener(private val activity: Activity,
                             private val user: FirebaseUser) : OnCompleteListener<Void> {

  override fun onComplete(task: Task<Void>) {
    if (task.isSuccessful) {
      activity.getSharedPreferences("pref", 0).edit().remove(user.uid).apply()
      activity.recreate()
      Log.d("FA", "User account deleted.")
    } else {
      Toast.makeText(activity.applicationContext, "Password isn't correct",
          Toast.LENGTH_SHORT).show()
    }
  }
}
