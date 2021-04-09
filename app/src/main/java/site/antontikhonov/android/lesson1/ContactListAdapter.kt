package site.antontikhonov.android.lesson1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

class ContactListAdapter(private val onItemClickAction: (String) -> Unit) : ListAdapter<Contact,
        ContactViewHolder>(DiffUtilCallback) {

       override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
              return ContactViewHolder(LayoutInflater.from(parent.context)
                      .inflate(R.layout.item_contact, parent, false)) {
                     position -> this.onItemClickAction(getItem(position).id)
              }
       }

       override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
              holder.bind(getItem(position))
       }

       interface OnItemClickListener {
              fun clickItem(id: String)
       }
}