package site.antontikhonov.android.application

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import site.antontikhonov.android.domain.contactlocation.ContactLocationRepository
import site.antontikhonov.android.presentation.data.ContactLocationRoomRepository
import site.antontikhonov.android.presentation.database.LocationDao
import site.antontikhonov.android.presentation.database.LocationDatabase
import javax.inject.Singleton

private const val DATABASE_NAME = "location_database"

@Module
class LocationDatabaseModule {
    @Singleton
    @Provides
    fun providesLocationDatabase(context: Context): LocationDatabase = Room.databaseBuilder(
        context,
        LocationDatabase::class.java,
        DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun providesLocationDao(database: LocationDatabase): LocationDao
        = database.locationDao()

    @Singleton
    @Provides
    fun providesLocationRepository(locationDao: LocationDao): ContactLocationRepository
        = ContactLocationRoomRepository(locationDao)
}