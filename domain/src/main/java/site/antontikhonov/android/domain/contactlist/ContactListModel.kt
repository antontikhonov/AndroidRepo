package site.antontikhonov.android.domain.contactlist

import site.antontikhonov.android.domain.ContactRepository

class ContactListModel(private val repository: ContactRepository)
    : ContactListInteractor {
    override fun loadContacts(name: String) = repository.readContacts(name)
}