package site.antontikhonov.android.lesson1

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract.Contacts
import android.provider.ContactsContract.CommonDataKinds
import android.provider.ContactsContract.Data

object ContactResolver {
    private val DATE_SEPARATOR: Regex = Regex("-+")
    private const val DATA_CONTACT_ID = "${Data.CONTACT_ID}="
    private const val FIND_CONTACT_BY_ID_SELECTION = "${Contacts._ID} = ?"
    private const val GET_LIST_PHONES_SELECTION = "${CommonDataKinds.Phone.CONTACT_ID} = ?"
    private const val GET_LIST_EMAILS_SELECTION = "${CommonDataKinds.Email.CONTACT_ID} = ?"
    private const val GET_BIRTHDAY_DATE_SELECTION = " AND ${Data.MIMETYPE}= '${CommonDataKinds.Event.CONTENT_ITEM_TYPE}' AND ${CommonDataKinds.Event.TYPE}=${CommonDataKinds.Event.TYPE_BIRTHDAY}"
    private const val GET_DESCRIPTION_SELECTION = "$DATA_CONTACT_ID ? AND ${Data.MIMETYPE} = ?"
    private const val GET_PHOTO_URI_SELECTION = " AND ${Data.MIMETYPE}='${CommonDataKinds.Photo.CONTENT_ITEM_TYPE}'"

    fun getContactsList(contentResolver: ContentResolver): List<Contact> {
        val contactsList = mutableListOf<Contact>()
        contentResolver.query(
            Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )?.use {
            while (it.moveToNext()) {
                val contact = it.toContactForList(contentResolver)
                if(contact != null) {
                    contactsList.add(contact)
                }
            }
        }
        return contactsList
    }

    fun findContactById(contentResolver: ContentResolver, id: String): Contact? {
        var contact: Contact? = null
        contentResolver.query(
            Contacts.CONTENT_URI,
            arrayOf(Contacts.DISPLAY_NAME),
            FIND_CONTACT_BY_ID_SELECTION,
            arrayOf(id),
            null
        )?.use {
            while (it.moveToNext()) {
                contact = it.toContact(contentResolver, id)
            }
        }
        return contact
    }

    private fun getListPhones(contentResolver: ContentResolver, id: String): List<String> {
        val listPhoneNumbers = mutableListOf<String>()
        contentResolver.query(
            CommonDataKinds.Phone.CONTENT_URI,
            null,
            GET_LIST_PHONES_SELECTION,
            arrayOf(id),
            null
        )?.use {
            while (it.moveToNext()) {
                val phoneNumber = it.getString(it.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                if(phoneNumber != null) {
                    listPhoneNumbers.add(phoneNumber)
                }
            }
        }
        return listPhoneNumbers
    }

    private fun getListEmails(contentResolver: ContentResolver, id: String): List<String> {
        val listEmails = mutableListOf<String>()
        contentResolver.query(
            CommonDataKinds.Email.CONTENT_URI,
            null,
            GET_LIST_EMAILS_SELECTION,
            arrayOf(id),
            null
        )?.use {
            while (it.moveToNext()) {
                val email = it.getString(it.getColumnIndex(CommonDataKinds.Email.DATA))
                if(email != null) {
                    listEmails.add(email)
                }
            }
        }
        return listEmails
    }

    private fun getBirthdayDate(contentResolver: ContentResolver, id: String): String? {
        var birthday: String? = null
        contentResolver.query(
            Data.CONTENT_URI,
            arrayOf(CommonDataKinds.Event.DATA),
            DATA_CONTACT_ID + id + GET_BIRTHDAY_DATE_SELECTION,
            null,
            null
        )?.use {
            while (it.moveToNext()) {
                birthday =
                    it.getString(it.getColumnIndex(CommonDataKinds.Event.START_DATE))
            }
        }
        return birthday
    }

    private fun getDescription(contentResolver: ContentResolver, id: String): String? {
        var description: String? = null
        contentResolver.query(
            Data.CONTENT_URI,
            null,
            GET_DESCRIPTION_SELECTION,
            arrayOf(id, CommonDataKinds.Note.CONTENT_ITEM_TYPE),
            null
        )?.use {
            while (it.moveToNext()) {
                description = it.getString(it.getColumnIndex(CommonDataKinds.Note.NOTE))
            }
        }
        return description
    }

    private fun getPhotoUri(contentResolver: ContentResolver, id: String): Uri? {
        var photoUri: Uri? = null
        contentResolver.query(
            Data.CONTENT_URI,
            null,
            DATA_CONTACT_ID + id + GET_PHOTO_URI_SELECTION,
            null,
            null
        )?.use {
            if (it.moveToFirst()) {
                val uriString = it.getString(it.getColumnIndex(Contacts.Photo.PHOTO_URI))
                if(uriString != null) {
                    photoUri = Uri.parse(uriString)
                }
            }
        }
        return photoUri
    }

    private fun Cursor.toContactForList(contentResolver: ContentResolver): Contact? {
        val id = getString(getColumnIndex(Contacts._ID)) ?: return null
        return Contact(
            id = id,
            name = getString(getColumnIndex(Contacts.DISPLAY_NAME)) ?: "",
            phoneList = getListPhones(contentResolver, id),
            emailList = null,
            description = null,
            dayOfBirthday = null,
            monthOfBirthday = null,
            image = getPhotoUri(contentResolver, id)
        )
    }

    private fun Cursor.toContact(contentResolver: ContentResolver, id: String): Contact {
        var dayOfBirthday: Int? = null
        var monthOfBirthday: Int? = null
        val birthdayList = getBirthdayDate(contentResolver, id)?.split(DATE_SEPARATOR)
        if(birthdayList?.size == 3) {
            monthOfBirthday = birthdayList[1].toIntOrNull()
            dayOfBirthday = birthdayList[2].toIntOrNull()
        }
        return Contact(
            id = id,
            name = getString(getColumnIndex(Contacts.DISPLAY_NAME)) ?: "",
            phoneList = getListPhones(contentResolver, id),
            emailList = getListEmails(contentResolver, id),
            description = getDescription(contentResolver, id),
            dayOfBirthday = dayOfBirthday,
            monthOfBirthday = monthOfBirthday,
            image = getPhotoUri(contentResolver, id)
        )
    }
}