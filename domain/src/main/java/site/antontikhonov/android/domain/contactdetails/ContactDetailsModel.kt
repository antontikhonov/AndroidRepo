package site.antontikhonov.android.domain.contactdetails

import io.reactivex.rxjava3.core.Single
import site.antontikhonov.android.domain.ContactRepository
import site.antontikhonov.android.domain.contactlocation.ContactLocationEntity
import site.antontikhonov.android.domain.contactlocation.ContactLocationRepository

class ContactDetailsModel(
    private val contactRepository: ContactRepository,
    private val contactLocationRepository: ContactLocationRepository
    ) : ContactDetailsInteractor {

    override fun loadContactById(id: String) = contactRepository.readContactById(id)

    override fun loadLocationById(id: String): Single<ContactLocationEntity>
            = contactLocationRepository.readLocationById(id)
}