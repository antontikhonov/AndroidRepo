package site.antontikhonov.android.presentation.data

import io.reactivex.rxjava3.core.Single
import site.antontikhonov.android.domain.contactlocation.ContactLocationEntity
import site.antontikhonov.android.domain.contactlocation.ContactLocationRepository
import site.antontikhonov.android.presentation.database.LocationDao
import site.antontikhonov.android.presentation.extensions.toContactLocationEntity
import site.antontikhonov.android.presentation.extensions.toContactLocationEntityList
import site.antontikhonov.android.presentation.extensions.toLocation
import javax.inject.Inject

class ContactLocationRoomRepository @Inject constructor(private val locationDao: LocationDao) : ContactLocationRepository {

    override fun readLocationById(id: String): Single<ContactLocationEntity>
        = locationDao.getLocationById(id)
            .map { it.toContactLocationEntity() }

    override fun readLocations(): Single<List<ContactLocationEntity>>
        = locationDao.getAllLocations()
            .map { it.toContactLocationEntityList() }

    override fun addLocation(contactLocationEntity: ContactLocationEntity): Single<ContactLocationEntity>
        = Single.fromCallable {
            locationDao.addLocation(contactLocationEntity.toLocation())
        }.map { contactLocationEntity }

    override fun updateLocation(contactLocationEntity: ContactLocationEntity): Single<ContactLocationEntity>
        = Single.fromCallable {
            locationDao.addLocation(contactLocationEntity.toLocation())
        } .map { contactLocationEntity }
}