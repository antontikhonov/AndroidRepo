package site.antontikhonov.android.application.contactdetails

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import site.antontikhonov.android.presentation.viewmodels.ContactDetailsViewModel
import site.antontikhonov.android.application.contactlist.ViewModelKey

@Module
abstract class ContactDetailsViewModelModule {
    @ContactsDetailsScope
    @Binds
    @IntoMap
    @ViewModelKey(ContactDetailsViewModel::class)
    abstract fun bindContactDetailsViewModel(viewModel: ContactDetailsViewModel): ViewModel
}