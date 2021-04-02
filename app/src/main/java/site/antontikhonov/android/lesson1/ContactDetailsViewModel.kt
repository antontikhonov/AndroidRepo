package site.antontikhonov.android.lesson1

import android.content.Context
import androidx.lifecycle.ViewModel

class ContactDetailsViewModel : ViewModel() {
       fun getContactById(context: Context, id: String) = ContactProviderRepository.loadContact(context, id)
}