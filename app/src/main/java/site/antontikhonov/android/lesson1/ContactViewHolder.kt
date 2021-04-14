package site.antontikhonov.android.lesson1

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactViewHolder(itemView: View, onItemClick: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
       private var contactImageView: ImageView = itemView.findViewById(R.id.contact_image)
       private var contactNameTextView: TextView = itemView.findViewById(R.id.contact_name)
       private var contactNumberTextView: TextView = itemView.findViewById(R.id.contact_num)

       init {
              itemView.setOnClickListener {
                     val position = adapterPosition
                     if(position != RecyclerView.NO_POSITION) {
                            onItemClick(position)
                     }
              }
       }

       fun bind(contact: Contact) {
              contactNameTextView.text = contact.name
              contactNumberTextView.text = if (contact.phoneList.isNotEmpty()) contact.phoneList[0] else ""
              val photoUri: Uri? = contact.image
              if (photoUri != null) {
                     contactImageView.setImageURI(photoUri)
              } else {
                     contactImageView.setImageResource(R.drawable.contact_placeholder)
              }
       }
}