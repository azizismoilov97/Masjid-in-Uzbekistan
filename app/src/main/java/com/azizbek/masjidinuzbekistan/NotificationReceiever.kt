package com.azizbek.masjidinuzbekistan

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.azizbek.masjidinuzbekistan.activities.MainActivity

class NotificationReceiever : BroadcastReceiver() {

    var bigTextStyle: NotificationCompat.BigTextStyle? = null

    override fun onReceive(context: Context, intent: Intent) {

        setBigTextStyle()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val repeatingIntent = Intent(context, MainActivity::class.java)

        repeatingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, repeatingIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        val icon = BitmapFactory.decodeResource(context.resources, R.drawable.first_image)
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notifimage)
                .setContentTitle("Namoz vaqtida farz qilingan")
                .setStyle(bigTextStyle)
                .setAutoCancel(true)
                .setLargeIcon(icon)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setFullScreenIntent(pendingIntent, true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
        var notificationChannel: NotificationChannel? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "EnglishforIT", importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            assert(notificationManager != null)
            builder.setChannelId(NOTIFICATION_CHANNEL_ID)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        assert(notificationManager != null)
        notificationManager.notify(100, builder.build())
    }

    private fun setBigTextStyle() {
        bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle!!.bigText("""
    Namozni mukammal ado etingiz, 
    zakot beringiz va ruku qiluvchilar bilan birga ruku qilingiz!
    
    Baqara surasi 43-oyat
    """.trimIndent())
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "10001"
    }
}