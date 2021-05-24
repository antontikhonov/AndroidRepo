package site.antontikhonov.android.application.contactmaps

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import site.antontikhonov.android.application.ViewModelKey
import site.antontikhonov.android.presentation.viewmodels.ContactMapsViewModel

@Module
abstract class ContactMapsViewModelModule {
    @ContactsMapsScope
    @Binds
    @IntoMap
    @ViewModelKey(ContactMapsViewModel::class)
    abstract fun bindContactMapsViewModel(viewModel: ContactMapsViewModel): ViewModel
}