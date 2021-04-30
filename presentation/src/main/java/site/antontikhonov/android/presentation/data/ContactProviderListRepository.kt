package site.antontikhonov.android.presentation.data

import android.content.Context
import io.reactivex.rxjava3.core.Single
import site.antontikhonov.android.domain.contactlist.ContactListEntity
import site.antontikhonov.android.domain.contactlist.ContactListRepository
import javax.inject.Inject

class ContactProviderListRepository @Inject constructor(private val context: Context):
    ContactListRepository {

    override fun readContacts(name: String): Single<List<ContactListEntity>> =
        Single.fromCallable { ContactResolver.getContactsList(context, name) }
}