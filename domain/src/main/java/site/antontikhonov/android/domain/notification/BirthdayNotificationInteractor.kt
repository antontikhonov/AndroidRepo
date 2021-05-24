package site.antontikhonov.android.domain.notification

interface BirthdayNotificationInteractor {
    fun switchBirthdayNotification(id: String, contactName: String, dayOfBirthday: Int, monthOfBirthday: Int)
    fun checkNotification(id: String): Boolean
}