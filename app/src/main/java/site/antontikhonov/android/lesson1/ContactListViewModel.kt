package site.antontikhonov.android.lesson1

import android.content.Context
import androidx.lifecycle.ViewModel

class ContactListViewModel : ViewModel() {
       fun getContactList(context: Context) = ContactProviderRepository.loadContactList(context)
}