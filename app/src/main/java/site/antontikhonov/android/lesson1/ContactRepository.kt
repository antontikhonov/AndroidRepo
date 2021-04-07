package site.antontikhonov.android.lesson1

import android.content.Context
import androidx.lifecycle.LiveData

interface ContactRepository {
       fun loadContactList(context: Context): LiveData<List<Contact>>
       fun loadContact(context: Context, id: String): LiveData<Contact>
}