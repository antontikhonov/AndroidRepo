package site.antontikhonov.android.presentation.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import site.antontikhonov.android.domain.notification.CalendarRepository
import site.antontikhonov.android.domain.notification.BirthdayNotificationInteractor
import site.antontikhonov.android.domain.notification.BirthdayNotificationRepository
import site.antontikhonov.android.presentation.MainActivity
import site.antontikhonov.android.presentation.R
import site.antontikhonov.android.presentation.di.HasComponent
import site.antontikhonov.android.presentation.fragments.EXTRA_CONTACT_ID
import site.antontikhonov.android.presentation.fragments.EXTRA_MESSAGE
import site.antontikhonov.android.presentation.fragments.EXTRA_NAME
import java.util.*
import javax.inject.Inject

const val CHANNEL_ID = "Channel_01"

class BirthdayReceiver : BroadcastReceiver() {
    private lateinit var contactId: String
    private lateinit var contactName: String
    private lateinit var message: String

    @Inject
    lateinit var calendarRepository: CalendarRepository
    @Inject
    lateinit var birthdayNotificationRepository: BirthdayNotificationRepository
    @Inject
    lateinit var birthdayNotificationInteractor: BirthdayNotificationInteractor

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as HasComponent).getAppComponent()
            .plusNotificationContainer()
            .inject(this)
        contactId = requireNotNull(intent.extras?.getString(EXTRA_CONTACT_ID))
        contactName = requireNotNull(intent.extras?.getString(EXTRA_NAME))
        message = requireNotNull(intent.extras?.getString(EXTRA_MESSAGE))
        createNotification(context, contactId)
    }


    private fun createNotification(context: Context, id: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
                .setContentText(message)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .build()
        notificationManager.notify(id.toInt(), notification)
        recreateNotification(id, contactName)
    }

    private fun recreateNotification(id: String, name: String) {
        val birthday: Calendar = calendarRepository.getNow()
        birthdayNotificationRepository.removeNotification(id)
        birthdayNotificationInteractor.switchBirthdayNotification(
            id = id,
            contactName = name,
            dayOfBirthday = birthday.get(Calendar.DAY_OF_MONTH),
            monthOfBirthday = birthday.get(Calendar.MONTH)
        )
    }
}