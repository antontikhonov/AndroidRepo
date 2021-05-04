package site.antontikhonov.android.domain.contactlist

class ContactListModel(private val repository: ContactListRepository)
    : ContactListInteractor {
    override fun loadContacts(name: String) = repository.readContacts(name)
}