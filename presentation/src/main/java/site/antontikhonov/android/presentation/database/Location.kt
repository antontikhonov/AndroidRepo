package site.antontikhonov.android.presentation.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Location(
    @PrimaryKey val id: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)