package site.antontikhonov.android.application.contactdetails

import dagger.Subcomponent
import site.antontikhonov.android.presentation.di.NotificationContainer

@ContactsDetailsScope
@Subcomponent(modules = [ContactDetailsModule::class])
interface NotificationComponent : NotificationContainer