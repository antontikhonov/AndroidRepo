package site.antontikhonov.android.lesson1

data class Contact(val name: String,
                   val firstNum: String,
                   val secondNum: String?,
                   val firstEmail: String?,
                   val secondEmail: String?,
                   val description: String?,
                   val image: Int)

val contacts = arrayListOf(Contact("Elon Musk", "+1 (310) 999 99 99", "+1 (666) 222 22 22", "elon.musk@gmail.com", "elon@musk.net", "Genius, Billionaire, Playboy, Philanthropist", R.drawable.elon))