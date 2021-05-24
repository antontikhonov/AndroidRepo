package site.antontikhonov.android.domain.notification

import java.util.*

interface BirthdayNotificationRepository {
    fun checkNotification(id: String): Boolean
    fun makeNotification(id: String, contactName: String, birthday: Calendar)
    fun removeNotification(id: String)
}