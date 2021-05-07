package site.antontikhonov.android.application.contactdetails

import dagger.Module
import dagger.Provides
import site.antontikhonov.android.domain.notification.CalendarRepository
import site.antontikhonov.android.domain.contactdetails.ContactDetailsInteractor
import site.antontikhonov.android.domain.contactdetails.ContactDetailsModel
import site.antontikhonov.android.domain.notification.BirthdayNotificationInteractor
import site.antontikhonov.android.domain.notification.BirthdayNotificationModel
import site.antontikhonov.android.domain.notification.BirthdayNotificationRepository
import site.antontikhonov.android.domain.ContactRepository
import site.antontikhonov.android.domain.contactlocation.ContactLocationRepository

@Module
class ContactDetailsModule {
    @ContactsDetailsScope
    @Provides
    fun providesContactDetailsInteractor(
        contactRepository: ContactRepository,
        contactLocationRepository: ContactLocationRepository
    ): ContactDetailsInteractor = ContactDetailsModel(contactRepository, contactLocationRepository)

    @ContactsDetailsScope
    @Provides
    fun providesBirthdayNotificationInteractor(
        birthdayNotificationRepository: BirthdayNotificationRepository,
        calendarRepository: CalendarRepository
    ) : BirthdayNotificationInteractor = BirthdayNotificationModel(birthdayNotificationRepository, calendarRepository)
}