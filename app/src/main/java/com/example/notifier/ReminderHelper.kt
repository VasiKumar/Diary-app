package com.example.notifier

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import java.util.Calendar

object ReminderHelper {
    const val CHANNEL_ID = "mindflow_reminders"
    const val GOAL_REMINDER_ID = 101
    const val DIARY_REMINDER_ID = 102
    
    fun createNotificationChannel(context: Context) {
        val name = "Daily MindFlow Reminders"
        val descriptionText = "Reminders for daily goals and personal diary reflection"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("MissingPermission")
    fun showNotification(context: Context, id: Int, title: String, message: String) {
        createNotificationChannel(context)
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, id, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // stock info drawable is highly reliable
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(id, builder.build())
            } catch (e: Exception) {
                Log.e("ReminderHelper", "Failed to show notification: ${e.message}")
            }
        }
    }

    fun scheduleDailyReminders(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 1. Goal reminder at 8:00 PM (20:00)
        scheduleReminder(context, alarmManager, GOAL_REMINDER_ID, 20, 0, "Goal Reminder")

        // 2. Diary reminder at 9:00 PM (21:00)
        scheduleReminder(context, alarmManager, DIARY_REMINDER_ID, 21, 0, "Diary Reminder")
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleReminder(
        context: Context,
        alarmManager: AlarmManager,
        reminderId: Int,
        hour: Int,
        minute: Int,
        action: String
    ) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            this.action = action
            putExtra("reminder_id", reminderId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        try {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
            Log.d("ReminderHelper", "Scheduled reminder: $action at $hour:$minute")
        } catch (e: Exception) {
            Log.e("ReminderHelper", "Failed to schedule reminder $action: ${e.message}")
        }
    }

    fun cancelAllReminders(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        cancelReminder(context, alarmManager, GOAL_REMINDER_ID, "Goal Reminder")
        cancelReminder(context, alarmManager, DIARY_REMINDER_ID, "Diary Reminder")
    }

    private fun cancelReminder(context: Context, alarmManager: AlarmManager, reminderId: Int, action: String) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            this.action = action
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d("ReminderHelper", "Canceled reminder: $action")
        }
    }
}
