package com.doapps.habits.helper

import android.app.Notification
import android.content.Context

interface NotificationBuilder {
 fun create(context: Context, str: String) : Notification
}