package com.dohabit.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.dohabit.BuildConfig;

public class FirebaseWifiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (BuildConfig.DEBUG) {
            if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d("FirebaseWifiReceiver", "Have Wifi Connection");
            } else {
                Log.d("FirebaseWifiReceiver", "Don't have Wifi Connection");
            }
        }
    }
}
