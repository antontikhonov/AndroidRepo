package site.antontikhonov.android.presentation

import android.app.AlarmManager
import android.app.PendingIntent
import site.antontikhonov.android.domain.notification.BirthdayNotificationRepository
import java.util.*
import kotlin.collections.HashMap

class BirthdayNotificationStub(
    private val alarmManager: AlarmManager,
    private val pendingIntent: PendingIntent
    ) : BirthdayNotificationRepository {

    private val notifications: HashMap<String, Calendar> = HashMap()

    override fun checkNotification(id: String): Boolean = !notifications.containsKey(id)

    override fun makeNotification(id: String, contactName: String, birthday: Calendar) {
        notifications[id] = birthday
        alarmManager.set(AlarmManager.RTC_WAKEUP, birthday.timeInMillis, pendingIntent)
    }

    override fun removeNotification(id: String) {
        notifications.remove(id)
        alarmManager.cancel(pendingIntent)
    }
}