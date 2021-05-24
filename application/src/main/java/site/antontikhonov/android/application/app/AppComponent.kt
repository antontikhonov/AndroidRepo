package site.antontikhonov.android.application.app

import dagger.Component
import site.antontikhonov.android.application.LocationDatabaseModule
import site.antontikhonov.android.application.contactdetails.ContactDetailsComponent
import site.antontikhonov.android.application.contactlist.ContactListComponent
import site.antontikhonov.android.application.contactdetails.NotificationComponent
import site.antontikhonov.android.application.ViewModelFactoryModule
import site.antontikhonov.android.application.contactmaps.ContactMapsComponent
import site.antontikhonov.android.presentation.di.AppContainer
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ViewModelFactoryModule::class, LocationDatabaseModule::class])
interface AppComponent : AppContainer {
    override fun plusContactListContainer(): ContactListComponent
    override fun plusContactDetailsContainer(): ContactDetailsComponent
    override fun plusNotificationContainer(): NotificationComponent
    override fun plusContactMapsContainer(): ContactMapsComponent
}