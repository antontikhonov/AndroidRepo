package site.antontikhonov.android.lesson1.di.components

import dagger.Subcomponent
import site.antontikhonov.android.lesson1.di.modules.ContactDetailsViewModelModule
import site.antontikhonov.android.lesson1.di.scopes.ContactsDetailsScope
import site.antontikhonov.android.lesson1.fragments.ContactDetailsFragment

@ContactsDetailsScope
@Subcomponent(modules = [ContactDetailsViewModelModule::class])
interface ContactDetailsComponent {
    fun inject(contactDetailsFragment: ContactDetailsFragment)
}