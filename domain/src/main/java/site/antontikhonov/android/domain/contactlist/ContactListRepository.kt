package site.antontikhonov.android.domain.contactlist

import io.reactivex.rxjava3.core.Single

interface ContactListRepository {
    fun readContacts(name: String): Single<List<ContactListEntity>>
}