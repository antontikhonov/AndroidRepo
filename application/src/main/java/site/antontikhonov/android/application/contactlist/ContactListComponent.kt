package site.antontikhonov.android.application.contactlist

import dagger.Subcomponent
import site.antontikhonov.android.presentation.di.ContactListContainer

@ContactsListScope
@Subcomponent(modules = [ContactListViewModelModule::class, ContactListModule::class])
interface ContactListComponent : ContactListContainer