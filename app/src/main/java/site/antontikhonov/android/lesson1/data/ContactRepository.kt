package site.antontikhonov.android.lesson1.data

import io.reactivex.rxjava3.core.Single
import site.antontikhonov.android.lesson1.models.Contact

interface ContactRepository {
       fun loadContactList(name: String): Single<List<Contact>>
       fun loadContact(id: String): Single<Contact>
}