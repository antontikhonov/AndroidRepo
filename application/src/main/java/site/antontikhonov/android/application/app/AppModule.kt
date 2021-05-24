package site.antontikhonov.android.application.app

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import site.antontikhonov.android.domain.notification.CalendarRepositoryImpl
import site.antontikhonov.android.domain.notification.CalendarRepository
import site.antontikhonov.android.domain.notification.BirthdayNotificationRepository
import site.antontikhonov.android.presentation.notification.BirthdayNotification
import site.antontikhonov.android.presentation.schedulers.BaseSchedulerProvider
import site.antontikhonov.android.presentation.schedulers.SchedulerProvider
import site.antontikhonov.android.domain.ContactRepository
import site.antontikhonov.android.presentation.data.ContactProviderRepository
import javax.inject.Singleton

@Module
class AppModule(private val application: Application) {
    @Singleton
    @Provides
    fun providesContext(): Context = application

    @Singleton
    @Provides
    fun providesContactRepository(context: Context): ContactRepository
        = ContactProviderRepository(context)

    @Singleton
    @Provides
    fun providesBirthdayNotificationRepository(): BirthdayNotificationRepository = BirthdayNotification(application)

    @Singleton
    @Provides
    fun providesCalendarRepository(): CalendarRepository = CalendarRepositoryImpl()

    @Singleton
    @Provides
    fun providesSchedulerProvider(): BaseSchedulerProvider = SchedulerProvider()
}