package site.antontikhonov.android.presentation.di

interface AppContainer {
    fun plusContactListContainer(): ContactListContainer
    fun plusContactDetailsContainer(): ContactDetailsContainer
    fun plusNotificationContainer(): NotificationContainer
}