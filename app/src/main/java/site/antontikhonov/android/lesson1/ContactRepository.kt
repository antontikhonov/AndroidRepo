package site.antontikhonov.android.lesson1

import android.content.Context
import io.reactivex.rxjava3.core.Single

interface ContactRepository {
       fun loadContactList(context: Context, name: String): Single<List<Contact>>
       fun loadContact(context: Context, id: String): Single<Contact>
}