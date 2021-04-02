package site.antontikhonov.android.lesson1

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object ContactProviderRepository : ContactRepository {
    private lateinit var contactList: MutableLiveData<List<Contact>>
    private lateinit var contact: MutableLiveData<Contact>

    override fun loadContactList(context: Context) : LiveData<List<Contact>> {
        if(!::contactList.isInitialized) {
            contactList = MutableLiveData()
        }
        Thread {
            contactList.postValue(ContactResolver.getContactsList(context))
        }.start()
        return contactList
    }

    override fun loadContact(context: Context, id: String) : LiveData<Contact> {
        if(!::contact.isInitialized) {
            contact = MutableLiveData()
        }
        Thread {
            contact.postValue(ContactResolver.findContactById(context, id))
        }.start()
        return contact
    }
}