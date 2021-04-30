package site.antontikhonov.android.application.contactlist

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import site.antontikhonov.android.application.ViewModelFactory

@Module
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}