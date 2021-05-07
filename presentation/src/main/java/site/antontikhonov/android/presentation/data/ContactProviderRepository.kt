package site.antontikhonov.android.presentation.data

import android.content.Context
import io.reactivex.rxjava3.core.Single
import site.antontikhonov.android.domain.contactdetails.ContactDetailsEntity
import site.antontikhonov.android.domain.ContactRepository
import site.antontikhonov.android.domain.contactlist.ContactListEntity
import javax.inject.Inject

class ContactProviderRepository @Inject constructor(private val context: Context) :
    ContactRepository {

    override fun readContactById(id: String): Single<ContactDetailsEntity> =
        Single.fromCallable { ContactResolver.findContactById(context, id) }

    override fun readContacts(name: String): Single<List<ContactListEntity>> =
        Single.fromCallable { ContactResolver.getContactsList(context, name) }
}