package com.doapps.habits.listeners

import com.doapps.habits.activity.MainActivity
import com.doapps.habits.models.IWeatherUpdater
import com.doapps.habits.models.INetworkStateListener
import com.google.firebase.auth.FirebaseAuth

class WeatherNetworkStateListener(private val weatherUpdater: IWeatherUpdater,
                                  private val activity: MainActivity) : INetworkStateListener {

  override fun update() {
    weatherUpdater.getWeather()
    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
      FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener {
        activity.onSetupNavigationDrawer(FirebaseAuth.getInstance().currentUser)
      }
    } else {
      activity.onSetupNavigationDrawer(user)
    }
  }
}
