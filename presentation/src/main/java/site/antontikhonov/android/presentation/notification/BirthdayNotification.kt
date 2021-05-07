package site.antontikhonov.android.presentation.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import site.antontikhonov.android.domain.notification.BirthdayNotificationRepository
import site.antontikhonov.android.presentation.R
import site.antontikhonov.android.presentation.fragments.EXTRA_CONTACT_ID
import site.antontikhonov.android.presentation.fragments.EXTRA_MESSAGE
import site.antontikhonov.android.presentation.fragments.EXTRA_NAME
import java.util.Calendar
import javax.inject.Inject

class BirthdayNotification @Inject constructor(private val context: Context)
    : BirthdayNotificationRepository {

    private val intent = Intent(context, BirthdayReceiver::class.java)
    private var alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun checkNotification(id: String) = PendingIntent.getBroadcast(
        context.applicationContext,
        id.hashCode(),
        intent,
        PendingIntent.FLAG_NO_CREATE
    ) == null

    override fun makeNotification(id: String, contactName: String, birthday: Calendar) {
        intent.putExtra(EXTRA_CONTACT_ID, id)
            .putExtra(EXTRA_MESSAGE, String.format(context.resources.getString(R.string.notification_text), contactName))
            .putExtra(EXTRA_NAME, contactName)
        val alarmIntent = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.set(AlarmManager.RTC_WAKEUP, birthday.timeInMillis, alarmIntent)
    }

    override fun removeNotification(id: String) = PendingIntent.getBroadcast(
        context,
        id.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    ).cancel()
}