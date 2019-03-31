package dev.tran.nam.chart.chartsong.controller

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import dev.tran.nam.chart.chartsong.R
import nam.tran.data.Logger
import javax.inject.Inject

class NotificationController @Inject constructor(mApp: Application) {

    private val ANDROID_CHANNEL_ID = NotificationController::class.java.simpleName + ".ANDROID"
    private val ANDROID_CHANNEL_NAME = "ANDROID CHANNEL"

    private var notificationManager = mApp.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    private var mBuilder: NotificationCompat.Builder

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create android channel
            val channel = NotificationChannel(
                ANDROID_CHANNEL_ID,
                ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW
            )

            // Sets whether notifications posted to this channel should display notification lights
            channel.enableLights(true)
            // Sets whether notification posted to this channel should vibrate.
            channel.enableVibration(false)
            channel.vibrationPattern = longArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
            // Sets the notification light color for notifications posted to this channel
            channel.lightColor = Color.BLACK
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.setSound(null, null)
            notificationManager.createNotificationChannel(channel)
        }

        mBuilder = NotificationCompat.Builder(mApp, ANDROID_CHANNEL_ID)
        mBuilder.setContentTitle("Player Song")
        mBuilder.setOngoing(true)
    }

    fun updatePlayerSong(id : Int,name : String,progress : Int,total : Int){
//        Logger.debug(progress)
//        Logger.debug(total)
        mBuilder.setContentText(name)
        mBuilder.setProgress(total,progress,false)
        mBuilder.setSmallIcon(R.drawable.icon_music_player)
        notificationManager.notify(id,mBuilder.build())
    }

    fun clearNotification(id : Int){
        notificationManager.cancel(id)
    }
}