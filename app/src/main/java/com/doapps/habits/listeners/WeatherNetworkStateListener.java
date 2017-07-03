package com.doapps.habits.listeners;

import android.app.Activity;

import com.doapps.habits.activity.MainActivity;
import com.doapps.habits.models.IWeatherUpdater;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WeatherNetworkStateListener {

    private final IWeatherUpdater weatherUpdater;
    private final Activity activity;

    public WeatherNetworkStateListener(IWeatherUpdater weatherUpdater, Activity activity) {
        this.weatherUpdater = weatherUpdater;
        this.activity = activity;
    }

    public void update() {
        weatherUpdater.getWeather();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(task ->
                    ((MainActivity) activity).onSetupNavigationDrawer(
                            FirebaseAuth.getInstance().getCurrentUser()));
        } else {
            ((MainActivity) activity).onSetupNavigationDrawer(user);
        }
    }
}
