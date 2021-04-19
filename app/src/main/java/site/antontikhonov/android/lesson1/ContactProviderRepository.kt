package site.antontikhonov.android.lesson1

import android.content.Context
import io.reactivex.rxjava3.core.Single

object ContactProviderRepository : ContactRepository {
    override fun loadContactList(context: Context, name: String) : Single<List<Contact>> =
        Single.fromCallable<List<Contact>> { ContactResolver.getContactsList(context, name) }

    override fun loadContact(context: Context, id: String): Single<Contact> =
        Single.fromCallable<Contact> { ContactResolver.findContactById(context, id) }
}