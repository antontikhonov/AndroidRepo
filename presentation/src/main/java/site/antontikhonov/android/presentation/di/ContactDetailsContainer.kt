package site.antontikhonov.android.presentation.di

import site.antontikhonov.android.presentation.fragments.ContactDetailsFragment

interface ContactDetailsContainer {
    fun inject(contactDetailsFragment: ContactDetailsFragment)
}