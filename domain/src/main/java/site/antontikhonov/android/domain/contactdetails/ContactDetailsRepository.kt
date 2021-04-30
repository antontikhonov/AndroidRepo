package site.antontikhonov.android.domain.contactdetails

import io.reactivex.rxjava3.core.Single

interface ContactDetailsRepository {
    fun readContactById(id: String): Single<ContactDetailsEntity>
}