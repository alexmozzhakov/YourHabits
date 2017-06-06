package com.doapps.habits.listeners;

import com.doapps.habits.activity.MainActivity;
import com.doapps.habits.fragments.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//TODO: change to something more abstract
public class WeatherNetworkStateListener {

    private final HomeFragment homeFragment;

    public WeatherNetworkStateListener(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    public void update() {
        homeFragment.getWeather();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(task -> {
                MainActivity ac = (MainActivity) homeFragment.getActivity();
                ac.onSetupNavigationDrawer(
                        FirebaseAuth.getInstance().getCurrentUser());
            });
        } else {
            ((MainActivity) homeFragment.getActivity()).onSetupNavigationDrawer(user);
        }
    }
}
