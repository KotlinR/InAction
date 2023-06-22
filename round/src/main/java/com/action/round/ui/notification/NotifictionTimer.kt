package com.action.round.ui.notification

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.action.round.R
import com.action.round.ui.screens.timer.TimerActivity

class NotificationTimer(
    private val context: Context,
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val TIMER_CHANNEL_ID = "timer_channel"
    }

    @SuppressLint("NotificationPermission")
    fun showNotification(liveData: String) {
        val activityIntent = Intent(context, TimerActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            0,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, TIMER_CHANNEL_ID)
            .setSmallIcon(R.drawable.timer_icon)
            .setContentTitle("Round master")
            .setContentText(liveData)
            .setContentIntent(activityPendingIntent)
            .setOngoing(true)
            .setShowWhen(false)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}