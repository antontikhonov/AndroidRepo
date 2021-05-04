package site.antontikhonov.android.domain.contactdetails

import io.reactivex.rxjava3.core.Single

interface ContactDetailsInteractor {
    fun loadContactById(id: String): Single<ContactDetailsEntity>
}