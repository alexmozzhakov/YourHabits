package com.doapps.habits.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.doapps.habits.listeners.WeatherNetworkStateListener;

public class ConnectionReceiver extends BroadcastReceiver {

    public static final String TAG = ConnectionReceiver.class.getSimpleName();
    private final WeatherNetworkStateListener observer;

    public ConnectionReceiver(WeatherNetworkStateListener observer) {
        this.observer = observer;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (info != null && info.isConnected()) {
            observer.update();
        }
    }
}
