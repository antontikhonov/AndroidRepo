package site.antontikhonov.android.presentation.di

import site.antontikhonov.android.presentation.fragments.ContactListFragment

interface ContactListContainer {
    fun inject(contactListFragment: ContactListFragment)
}