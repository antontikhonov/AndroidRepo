package site.antontikhonov.android.application.contactdetails

import dagger.Subcomponent
import site.antontikhonov.android.presentation.di.ContactDetailsContainer

@ContactsDetailsScope
@Subcomponent(modules = [ContactDetailsViewModelModule::class, ContactDetailsModule::class])
interface ContactDetailsComponent : ContactDetailsContainer