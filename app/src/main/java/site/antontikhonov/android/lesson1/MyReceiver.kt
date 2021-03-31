package site.antontikhonov.android.lesson1

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import java.util.*

const val CHANNEL_ID = "Channel_01"

class MyReceiver : BroadcastReceiver() {
    private var notificationManager: NotificationManager? = null
    private var contactId: String = "0"
    private lateinit var contactName: String

    override fun onReceive(context: Context, intent: Intent) {
        contactId = intent.extras?.getString(EXTRA_CONTACT_ID) ?: throw IllegalArgumentException("Contact ID required")
        contactName = intent.extras?.getString(EXTRA_NAME) ?: throw IllegalArgumentException("Contact name required")
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        createNotificationChannel(context)
        createNotification(context, contactId)
        val pendingIntent = PendingIntent.getBroadcast(context, contactId.toInt(), intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextCalendarBirthday().timeInMillis, pendingIntent)
    }

    private fun createNotificationChannel(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, context.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(context: Context, id: String) {
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.putExtra(EXTRA_CONTACT_ID, id)
        notificationIntent.putExtra(EXTRA_START_CHECK_PERMISSION, false)
        val pendingIntent = PendingIntent.getActivity(context, id.toInt(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.contact)
            .setContentText(context.getString(R.string.notification_text, contactName))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager?.notify(id.toInt(), notification)
    }

    private fun nextCalendarBirthday(): Calendar {
        val calendar = GregorianCalendar.getInstance()
        if(calendar.get(Calendar.MONTH) == Calendar.FEBRUARY && calendar.get(Calendar.DAY_OF_MONTH) == 29) {
            calendar.add(Calendar.YEAR, 4)
        } else {
            calendar.add(Calendar.YEAR, 1)
        }
        return calendar
    }
}



