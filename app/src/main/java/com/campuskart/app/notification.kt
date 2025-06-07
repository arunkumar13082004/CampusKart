package com.campuskart.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class notification : FirebaseMessagingService() {

    val channelName = "CampusKart Notification"
    val channelId = "com.campuskart.app"

    fun generateNotification(title: String, message: String) {

        // Creating Intent so that clicking the notification redirects to the app
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // ✅ Fixed: Added FLAG_IMMUTABLE to avoid Lint error on Android 12+
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // getRemoteView function to pass RemoteViews
        fun getRemoteView(title: String, message: String): RemoteViews {
            val remoteView = RemoteViews("com.campuskart.app", R.layout.notification_layout)
            remoteView.setTextViewText(R.id.name, title)
            remoteView.setTextViewText(R.id.message, message)
            return remoteView
        }

        // Creating Notification
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.notificationbell)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setContent(getRemoteView(title, message))

        // Notification Manager
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0, builder.build())
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            generateNotification(it.title ?: "Notification", it.body ?: "")
        }
    }
}
