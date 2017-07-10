package com.doapps.habits.helper

import android.content.Context
import android.net.ConnectivityManager

object ConnectionManager {

  /**
   * @return true if user is connected to Internet
   */
  @JvmStatic
  fun isConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
  }
}
