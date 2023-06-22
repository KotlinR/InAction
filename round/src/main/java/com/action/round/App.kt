package com.action.round

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import com.action.round.ui.notification.NotificationTimer

class App : Application() {

    val dependencies: Dependencies by lazy { Dependencies(this) }

    override fun onCreate() {
        super.onCreate()
        dependencies
        createdNotificationChannel()
    }

    private fun createdNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationTimer.TIMER_CHANNEL_ID,
                "Timer notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.apply {
                description = "My channel description"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(false)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}