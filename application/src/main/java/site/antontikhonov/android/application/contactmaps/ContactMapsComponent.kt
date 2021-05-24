package site.antontikhonov.android.application.contactmaps

import dagger.Subcomponent
import site.antontikhonov.android.presentation.di.ContactMapsContainer

@ContactsMapsScope
@Subcomponent(modules = [ContactMapsViewModelModule::class, ContactMapsModule::class])
interface ContactMapsComponent : ContactMapsContainer