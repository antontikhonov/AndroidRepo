package site.antontikhonov.android.domain.contactlocation

data class ContactLocationEntity(
    val id: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)