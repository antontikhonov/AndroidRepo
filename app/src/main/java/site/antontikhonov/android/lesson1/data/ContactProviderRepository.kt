package site.antontikhonov.android.lesson1.data

import android.content.Context
import io.reactivex.rxjava3.core.Single
import site.antontikhonov.android.lesson1.models.Contact

class ContactProviderRepository(private val context: Context): ContactRepository {
    override fun loadContactList(name: String): Single<List<Contact>> =
        Single.fromCallable { ContactResolver.getContactsList(context, name) }

    override fun loadContact(id: String): Single<Contact> =
        Single.fromCallable { ContactResolver.findContactById(context, id) }
}