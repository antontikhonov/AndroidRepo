package site.antontikhonov.android.presentation.extensions

import site.antontikhonov.android.domain.contactlocation.ContactLocationEntity
import site.antontikhonov.android.presentation.database.Location

fun Location.toContactLocationEntity(): ContactLocationEntity
    = ContactLocationEntity(id, address, latitude, longitude)

fun ContactLocationEntity.toLocation(): Location
    = Location(id, address, latitude, longitude)

fun List<Location>.toContactLocationEntityList(): List<ContactLocationEntity>
    = map { it.toContactLocationEntity() }