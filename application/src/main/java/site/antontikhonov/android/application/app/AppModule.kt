package site.antontikhonov.android.application.app

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import site.antontikhonov.android.domain.contactdetails.ContactDetailsRepository
import site.antontikhonov.android.domain.contactlist.ContactListRepository
import site.antontikhonov.android.presentation.data.ContactProviderDetailsRepository
import site.antontikhonov.android.presentation.data.ContactProviderListRepository
import javax.inject.Singleton

@Module
class AppModule(private val application: Application) {

    @Singleton
    @Provides
    fun providesContext(): Context = application

    @Singleton
    @Provides
    fun providesListRepository(): ContactListRepository = ContactProviderListRepository(application)

    @Singleton
    @Provides
    fun providesDetailsRepository(): ContactDetailsRepository = ContactProviderDetailsRepository(application)
}