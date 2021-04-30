package site.antontikhonov.android.domain.contactlist

data class ContactListEntity(
    val id: String,
    val name: String,
    val phoneList: List<String>,
    val image: String?
)