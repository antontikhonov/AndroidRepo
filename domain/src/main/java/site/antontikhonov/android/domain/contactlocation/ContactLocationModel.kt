package site.antontikhonov.android.domain.contactlocation

import io.reactivex.rxjava3.core.Single

class ContactLocationModel(private val repository: ContactLocationRepository)
    : ContactLocationInteractor {

    override fun loadLocationById(id: String): Single<ContactLocationEntity>
        = repository.readLocationById(id)

    override fun loadLocations(): Single<List<ContactLocationEntity>>
        = repository.readLocations()

    override fun addContactLocation(contactLocationEntity: ContactLocationEntity): Single<ContactLocationEntity>
        = repository.addLocation(contactLocationEntity)

    override fun updateContactLocation(contactLocationEntity: ContactLocationEntity): Single<ContactLocationEntity>
        = repository.updateLocation(contactLocationEntity)
}