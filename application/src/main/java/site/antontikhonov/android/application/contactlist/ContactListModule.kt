package site.antontikhonov.android.application.contactlist

import dagger.Module
import dagger.Provides
import site.antontikhonov.android.domain.ContactRepository
import site.antontikhonov.android.domain.contactlist.ContactListInteractor
import site.antontikhonov.android.domain.contactlist.ContactListModel

@Module
class ContactListModule {
    @ContactsListScope
    @Provides
    fun providesContactListInteractor(repository: ContactRepository): ContactListInteractor
        = ContactListModel(repository)
}