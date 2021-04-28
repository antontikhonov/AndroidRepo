package site.antontikhonov.android.lesson1.receivers

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import site.antontikhonov.android.lesson1.MainActivity
import site.antontikhonov.android.lesson1.R
import site.antontikhonov.android.lesson1.fragments.EXTRA_CONTACT_ID
import site.antontikhonov.android.lesson1.fragments.EXTRA_NAME
import java.util.*

const val CHANNEL_ID = "Channel_01"

class BirthdayReceiver : BroadcastReceiver() {
    private var contactId: String = "0"
    private lateinit var contactName: String

    override fun onReceive(context: Context, intent: Intent) {
        contactId = requireNotNull(intent.extras?.getString(EXTRA_CONTACT_ID))
        contactName = requireNotNull(intent.extras?.getString(EXTRA_NAME))
        createNotification(context, contactId)
        setNewBirthdayReceiver(context, intent)
    }

    private fun createNotification(context: Context, id: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val notificationIntent = Intent(context, MainActivity::class.java)
                .putExtra(EXTRA_CONTACT_ID, id)
        val notificationPendingIntent = PendingIntent.getActivity(
                context,
                id.toInt(),
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.contact_placeholder)
                .setContentText(context.getString(R.string.notification_text, contactName))
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .build()
        notificationManager?.notify(id.toInt(), notification)
    }

    private fun setNewBirthdayReceiver(context: Context, intent: Intent) {
        val pendingIntent = PendingIntent.getBroadcast(context, contactId.toInt(), intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextCalendarBirthday().timeInMillis, pendingIntent)
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