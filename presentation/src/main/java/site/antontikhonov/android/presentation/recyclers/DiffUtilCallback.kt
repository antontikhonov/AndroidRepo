package site.antontikhonov.android.presentation.recyclers

import androidx.recyclerview.widget.DiffUtil
import site.antontikhonov.android.domain.contactlist.ContactListEntity

object DiffUtilCallback : DiffUtil.ItemCallback<ContactListEntity>() {
       override fun areItemsTheSame(oldItem: ContactListEntity, newItem: ContactListEntity): Boolean {
              return oldItem.id == newItem.id
       }

       override fun areContentsTheSame(oldItem: ContactListEntity, newItem: ContactListEntity): Boolean {
              return if(oldItem.phoneList.isNotEmpty() && newItem.phoneList.isNotEmpty()) {
                     oldItem.name == newItem.name &&
                             oldItem.phoneList[0] == newItem.phoneList[0] && oldItem.image == newItem.image
              } else {
                     oldItem.name == newItem.name && oldItem.image == newItem.image
              }
       }
}