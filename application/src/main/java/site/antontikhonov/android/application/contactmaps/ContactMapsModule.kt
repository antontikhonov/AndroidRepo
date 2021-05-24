package site.antontikhonov.android.application.contactmaps

import dagger.Module
import dagger.Provides
import site.antontikhonov.android.domain.contactlocation.ContactLocationInteractor
import site.antontikhonov.android.domain.contactlocation.ContactLocationModel
import site.antontikhonov.android.domain.contactlocation.ContactLocationRepository

@Module
class ContactMapsModule {
    @ContactsMapsScope
    @Provides
    fun providesContactMapsInteractor(repository: ContactLocationRepository): ContactLocationInteractor
        = ContactLocationModel(repository)
}