package com.example.notifier

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra("reminder_id", ReminderHelper.GOAL_REMINDER_ID)
        val action = intent.action

        when (action) {
            "Goal Reminder" -> {
                ReminderHelper.showNotification(
                    context = context,
                    id = id,
                    title = "Did you complete today's goals?",
                    message = "Check off your daily checklist and stay consistent on your streak!"
                )
            }
            "Diary Reminder" -> {
                ReminderHelper.showNotification(
                    context = context,
                    id = id,
                    title = "Don't forget to write today's diary.",
                    message = "Take a peaceful moment to write a note and track your state of mind today."
                )
            }
        }
    }
}
