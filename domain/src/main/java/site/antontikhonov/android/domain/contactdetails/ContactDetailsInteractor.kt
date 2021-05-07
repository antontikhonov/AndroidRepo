package site.antontikhonov.android.domain.contactdetails

import io.reactivex.rxjava3.core.Single
import site.antontikhonov.android.domain.contactlocation.ContactLocationEntity

interface ContactDetailsInteractor {
    fun loadContactById(id: String): Single<ContactDetailsEntity>
    fun loadLocationById(id: String): Single<ContactLocationEntity>
}