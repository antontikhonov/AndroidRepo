package site.antontikhonov.android.application.contactdetails

import dagger.Module
import dagger.Provides
import site.antontikhonov.android.domain.notification.CalendarRepository
import site.antontikhonov.android.domain.contactdetails.ContactDetailsInteractor
import site.antontikhonov.android.domain.contactdetails.ContactDetailsModel
import site.antontikhonov.android.domain.contactdetails.ContactDetailsRepository
import site.antontikhonov.android.domain.notification.BirthdayNotificationInteractor
import site.antontikhonov.android.domain.notification.BirthdayNotificationModel
import site.antontikhonov.android.domain.notification.BirthdayNotificationRepository

@Module
class ContactDetailsModule {
    @ContactsDetailsScope
    @Provides
    fun providesContactDetailsInteractor(repository: ContactDetailsRepository): ContactDetailsInteractor
        = ContactDetailsModel(repository)

    @ContactsDetailsScope
    @Provides
    fun providesBirthdayNotificationInteractor(
        birthdayNotificationRepository: BirthdayNotificationRepository,
        calendarRepository: CalendarRepository
    ) : BirthdayNotificationInteractor = BirthdayNotificationModel(birthdayNotificationRepository, calendarRepository)
}