package site.antontikhonov.android.presentation.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.rxjava3.core.Single

@Dao
interface LocationDao {
    @Query("SELECT * FROM location WHERE id = :id")
    fun getLocationById(id: String): Single<Location>

    @Query("SELECT * FROM location")
    fun getAllLocations(): Single<List<Location>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addLocation(location: Location)

    @Update
    fun updateLocation(location: Location)
}