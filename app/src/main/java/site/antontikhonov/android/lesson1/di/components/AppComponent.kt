package site.antontikhonov.android.lesson1.di.components

import dagger.Component
import site.antontikhonov.android.lesson1.di.modules.AppModule
import site.antontikhonov.android.lesson1.di.modules.ViewModelFactoryModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ViewModelFactoryModule::class])
interface AppComponent {
    fun plusContactListComponent(): ContactListComponent
    fun plusContactDetailsComponent(): ContactDetailsComponent
}