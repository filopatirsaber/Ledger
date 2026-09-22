package com.ledger.reminders

import android.Manifest
import android.app.*
import android.content.*
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.webkit.*
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : Activity() {
    private lateinit var web: WebView
    private val channelId = "ledger_reminders"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createChannel()
        requestNotificationPermission()

        web = WebView(this)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.mediaPlaybackRequiresUserGesture = false
        web.addJavascriptInterface(AndroidBridge(this), "Android")
        web.loadUrl("file:///android_asset/reminders.html")
        setContentView(web)

        if (Build.VERSION.SDK_INT >= 31) {
            val alarm = getSystemService(AlarmManager::class.java)
            if (!alarm.canScheduleExactAlarms()) {
                // User can enable exact alarms in system settings if desired.
                Toast.makeText(this, "Enable exact alarms for precise reminders", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                channelId, "Ledger reminders", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminder notifications"
                enableVibration(true)
                setSound(
                    android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION),
                    android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 10)
        }
    }

    class AndroidBridge(private val context: Context) {
        @JavascriptInterface
        fun schedule(id: String, title: String, notes: String, dueMillis: Long) {
            ReminderScheduler.schedule(context, id, title, notes, dueMillis)
        }

        @JavascriptInterface
        fun cancel(id: String) {
            ReminderScheduler.cancel(context, id)
        }
    }
}
