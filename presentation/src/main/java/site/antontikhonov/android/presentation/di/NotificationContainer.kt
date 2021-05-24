package site.antontikhonov.android.presentation.di

import site.antontikhonov.android.presentation.notification.BirthdayReceiver

interface NotificationContainer {
    fun inject(notificationReceiver: BirthdayReceiver)
}