package site.antontikhonov.android.domain.contactlocation

import io.reactivex.rxjava3.core.Single

interface ContactLocationRepository {
    fun readLocationById(id: String): Single<ContactLocationEntity>

    fun readLocations(): Single<List<ContactLocationEntity>>

    fun addLocation(contactLocationEntity: ContactLocationEntity): Single<ContactLocationEntity>

    fun updateLocation(contactLocationEntity: ContactLocationEntity): Single<ContactLocationEntity>
}