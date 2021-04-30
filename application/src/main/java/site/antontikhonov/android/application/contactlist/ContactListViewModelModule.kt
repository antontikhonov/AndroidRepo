package site.antontikhonov.android.application.contactlist

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import site.antontikhonov.android.presentation.viewmodels.ContactListViewModel

@Module
abstract class ContactListViewModelModule {
    @ContactsListScope
    @Binds
    @IntoMap
    @ViewModelKey(ContactListViewModel::class)
    abstract fun bindContactDetailsViewModel(viewModel: ContactListViewModel): ViewModel
}