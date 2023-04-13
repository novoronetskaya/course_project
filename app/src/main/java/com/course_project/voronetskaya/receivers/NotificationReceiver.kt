package com.course_project.voronetskaya.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.course_project.voronetskaya.R
import java.util.Date

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) {
            return
        }

        val notId = intent?.extras?.getInt("notId") ?: return
        val treatId = intent.extras?.getString("treatId") ?: return
        val treatName = intent.extras?.getString("treatName") ?: return
        val time = intent.extras?.getLong("time") ?: return

        val missIntent = Intent(context, NotificationResultReceiver::class.java).apply {
            putExtra("notId", notId)
            putExtra("treatId", treatId)
            putExtra("treatName", treatName)
            putExtra("Result", "MISSED")
            putExtra("time", time)
        }

        val postponeIntent = Intent(context, NotificationResultReceiver::class.java).apply {
            putExtra("notId", notId)
            putExtra("treatId", treatId)
            putExtra("treatName", treatName)
            putExtra("Result", "POSTPONED")
            putExtra("time", time)
        }

        val takeIntent = Intent(context, NotificationResultReceiver::class.java).apply {
            putExtra("notId", notId)
            putExtra("treatId", treatId)
            putExtra("treatName", treatName)
            putExtra("Result", "TAKEN")
            putExtra("time", time)
        }

        val notification = NotificationCompat.Builder(context, "1")
            .setContentText(String.format("Примите лекарство %s", treatName))
            .setSmallIcon(R.drawable.auth_screen_icon)
            .setContentTitle("Органайзер лекарств")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .addAction(
                R.drawable.plus_icon,
                "Принять",
                PendingIntent.getBroadcast(context, Date().time.toInt(), takeIntent, PendingIntent.FLAG_IMMUTABLE)
            )
            .addAction(
                R.drawable.delete_icon,
                "Пропустить",
                PendingIntent.getBroadcast(context, Date().time.toInt(), missIntent, PendingIntent.FLAG_IMMUTABLE)
            ).addAction(
                R.drawable.clock_icon,
                "Отложить",
                PendingIntent.getBroadcast(context, Date().time.toInt(), postponeIntent, PendingIntent.FLAG_IMMUTABLE)
            ).setAutoCancel(true)
        val manager = NotificationManagerCompat.from(context)
        manager.notify(notId, notification.build())
    }
}