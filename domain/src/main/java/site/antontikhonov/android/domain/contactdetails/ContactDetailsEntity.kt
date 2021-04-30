package site.antontikhonov.android.domain.contactdetails

data class ContactDetailsEntity(
    val id: String,
    val name: String,
    val phoneList: List<String>,
    val emailList: List<String>,
    val description: String,
    val dayOfBirthday: Int,
    val monthOfBirthday: Int,
    val image: String?
)