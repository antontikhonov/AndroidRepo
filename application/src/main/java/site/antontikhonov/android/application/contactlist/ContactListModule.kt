package site.antontikhonov.android.application.contactlist

import dagger.Module
import dagger.Provides
import site.antontikhonov.android.domain.contactlist.ContactListInteractor
import site.antontikhonov.android.domain.contactlist.ContactListModel
import site.antontikhonov.android.domain.contactlist.ContactListRepository

@Module
class ContactListModule {
    @ContactsListScope
    @Provides
    fun providesContactListInteractor(repository: ContactListRepository): ContactListInteractor
        = ContactListModel(repository)
}