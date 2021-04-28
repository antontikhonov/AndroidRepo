package site.antontikhonov.android.lesson1.di.components

import dagger.Subcomponent
import site.antontikhonov.android.lesson1.fragments.ContactListFragment
import site.antontikhonov.android.lesson1.di.modules.ContactListViewModelModule
import site.antontikhonov.android.lesson1.di.scopes.ContactsListScope

@ContactsListScope
@Subcomponent(modules = [ContactListViewModelModule::class])
interface ContactListComponent {
    fun inject(contactListFragment: ContactListFragment)
}