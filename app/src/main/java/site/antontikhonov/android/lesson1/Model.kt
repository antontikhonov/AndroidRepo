package site.antontikhonov.android.lesson1

import android.net.Uri

data class Contact(
    val id: String,
    val name: String,
    val phoneList: List<String>,
    val emailList: List<String>?,
    val description: String?,
    val dayOfBirthday: Int?,
    val monthOfBirthday: Int?,
    val image: Uri?
)