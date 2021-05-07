package site.antontikhonov.android.domain.contactlocation

import io.reactivex.rxjava3.core.Single

interface ContactLocationInteractor {
    fun loadLocationById(id: String): Single<ContactLocationEntity>
    fun loadLocations(): Single<List<ContactLocationEntity>>
    fun addContactLocation(contactLocationEntity: ContactLocationEntity): Single<ContactLocationEntity>
    fun updateContactLocation(contactLocationEntity: ContactLocationEntity): Single<ContactLocationEntity>
}