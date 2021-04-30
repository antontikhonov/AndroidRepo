package site.antontikhonov.android.application.contactdetails

import dagger.Module
import dagger.Provides
import site.antontikhonov.android.domain.contactdetails.ContactDetailsInteractor
import site.antontikhonov.android.domain.contactdetails.ContactDetailsModel
import site.antontikhonov.android.domain.contactdetails.ContactDetailsRepository

@Module
class ContactDetailsModule {
    @ContactsDetailsScope
    @Provides
    fun providesContactDetailsInteractor(repository: ContactDetailsRepository): ContactDetailsInteractor
        = ContactDetailsModel(repository)
}