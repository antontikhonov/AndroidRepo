package site.antontikhonov.android.domain

import io.reactivex.rxjava3.core.Single
import site.antontikhonov.android.domain.contactdetails.ContactDetailsEntity
import site.antontikhonov.android.domain.contactlist.ContactListEntity

interface ContactRepository {
    fun readContactById(id: String): Single<ContactDetailsEntity>
    fun readContacts(name: String): Single<List<ContactListEntity>>
}