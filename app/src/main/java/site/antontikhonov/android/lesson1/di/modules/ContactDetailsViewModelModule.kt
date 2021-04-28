package site.antontikhonov.android.lesson1.di.modules

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import site.antontikhonov.android.lesson1.viewmodels.ContactDetailsViewModel
import site.antontikhonov.android.lesson1.di.ViewModelKey
import site.antontikhonov.android.lesson1.di.scopes.ContactsDetailsScope

@Module
abstract class ContactDetailsViewModelModule {

    @ContactsDetailsScope
    @Binds
    @IntoMap
    @ViewModelKey(ContactDetailsViewModel::class)
    abstract fun bindContactDetailsViewModel(viewModel: ContactDetailsViewModel): ViewModel
}