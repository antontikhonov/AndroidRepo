package site.antontikhonov.android.lesson1.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import site.antontikhonov.android.lesson1.data.ContactProviderRepository
import site.antontikhonov.android.lesson1.data.ContactRepository
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Singleton
    @Provides
    fun providesContext() = context

    @Singleton
    @Provides
    fun providesRepository(): ContactRepository = ContactProviderRepository(context)
}