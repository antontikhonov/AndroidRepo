package site.antontikhonov.android.presentation.di

import site.antontikhonov.android.presentation.fragments.ContactMapsFragment

interface ContactMapsContainer {
    fun inject(contactMapsFragment: ContactMapsFragment)
}