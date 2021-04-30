package site.antontikhonov.android.presentation.data

import android.content.Context
import io.reactivex.rxjava3.core.Single
import site.antontikhonov.android.domain.contactdetails.ContactDetailsEntity
import site.antontikhonov.android.domain.contactdetails.ContactDetailsRepository
import javax.inject.Inject

class ContactProviderDetailsRepository @Inject constructor(private val context: Context) :
    ContactDetailsRepository {

    override fun readContactById(id: String): Single<ContactDetailsEntity> =
        Single.fromCallable { ContactResolver.findContactById(context, id) }
}