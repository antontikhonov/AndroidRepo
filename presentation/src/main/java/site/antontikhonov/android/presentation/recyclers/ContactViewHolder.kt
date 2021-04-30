package site.antontikhonov.android.presentation.recyclers

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import site.antontikhonov.android.domain.contactlist.ContactListEntity
import site.antontikhonov.android.presentation.R

class ContactViewHolder(itemView: View, onItemClick: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
       private var contactImageView: ImageView = itemView.findViewById(R.id.contact_image)
       private var contactNameTextView: TextView = itemView.findViewById(R.id.contact_name)
       private var contactNumberTextView: TextView = itemView.findViewById(R.id.contact_num)

       init {
              itemView.setOnClickListener {
                     val position = bindingAdapterPosition
                     if(position != RecyclerView.NO_POSITION) {
                            onItemClick(position)
                     }
              }
       }

       fun bind(contact: ContactListEntity) {
              contactNameTextView.text = contact.name
              contactNumberTextView.text = if (contact.phoneList.isNotEmpty()) contact.phoneList[0] else ""
              if (contact.image != null) {
                     contactImageView.setImageURI(Uri.parse(contact.image))
              } else {
                     contactImageView.setImageResource(R.drawable.contact_placeholder)
              }
       }
}