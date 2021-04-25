package site.antontikhonov.android.lesson1.di.modules

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import site.antontikhonov.android.lesson1.viewmodels.ContactListViewModel
import site.antontikhonov.android.lesson1.di.ViewModelKey
import site.antontikhonov.android.lesson1.di.scopes.ContactsListScope

@Module
abstract class ContactListViewModelModule {

    @ContactsListScope
    @Binds
    @IntoMap
    @ViewModelKey(ContactListViewModel::class)
    abstract fun bindContactDetailsViewModel(viewModel: ContactListViewModel): ViewModel
}