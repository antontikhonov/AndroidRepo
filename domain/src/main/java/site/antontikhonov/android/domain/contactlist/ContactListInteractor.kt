package site.antontikhonov.android.domain.contactlist

import io.reactivex.rxjava3.core.Single

interface ContactListInteractor {
    fun loadContacts(name: String): Single<List<ContactListEntity>>
}