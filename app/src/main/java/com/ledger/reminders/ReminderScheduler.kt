package com.ledger.reminders

import android.app.*
import android.content.*
import android.os.Build

object ReminderScheduler {
    private const val CHANNEL = "ledger_reminders"

    fun schedule(context: Context, id: String, title: String, notes: String, dueMillis: Long) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("id", id)
            putExtra("title", title)
            putExtra("notes", notes)
        }
        val pi = PendingIntent.getBroadcast(
            context, id.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= 23) {
            alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dueMillis, pi)
        } else {
            alarm.setExact(AlarmManager.RTC_WAKEUP, dueMillis, pi)
        }
    }

    fun cancel(context: Context, id: String) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context, id.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarm.cancel(pi)
    }
}
