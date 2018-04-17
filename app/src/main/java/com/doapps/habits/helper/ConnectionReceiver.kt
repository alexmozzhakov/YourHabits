package com.doapps.habits.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import com.doapps.habits.models.INetworkStateListener

class ConnectionReceiver(private val observer: INetworkStateListener) : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val info = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
    if (info != null && info.isConnected) observer.update()
  }
}
