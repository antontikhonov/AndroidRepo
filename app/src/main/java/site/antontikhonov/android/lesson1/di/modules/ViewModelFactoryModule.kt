package site.antontikhonov.android.lesson1.di.modules

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import site.antontikhonov.android.lesson1.di.ViewModelFactory

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}