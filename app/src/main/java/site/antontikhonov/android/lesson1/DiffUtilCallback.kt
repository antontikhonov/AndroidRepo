package site.antontikhonov.android.lesson1

import androidx.recyclerview.widget.DiffUtil

object DiffUtilCallback : DiffUtil.ItemCallback<Contact>() {
       override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
              return oldItem.id == newItem.id
       }

       override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
              return if(oldItem.phoneList.isNotEmpty() && newItem.phoneList.isNotEmpty()) {
                     oldItem.name == newItem.name &&
                             oldItem.phoneList[0] == newItem.phoneList[0] && oldItem.image == newItem.image
              } else {
                     oldItem.name == newItem.name && oldItem.image == newItem.image
              }
       }
}