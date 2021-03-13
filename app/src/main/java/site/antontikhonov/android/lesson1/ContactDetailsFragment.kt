package site.antontikhonov.android.lesson1

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

private const val EXTRA_CONTACT_ID = "CONTACT_ID"

class ContactDetailsFragment : Fragment() {

    private var serviceInterface: ContactService.ServiceInterface? = null
    private var contactId: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is ContactService.ServiceInterface) {
            serviceInterface = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.contact_details_title)
        contactId = arguments?.getInt(EXTRA_CONTACT_ID) ?: throw IllegalArgumentException("Contact ID required")
        loadContactById()
    }

    fun loadContactById() = serviceInterface?.getService()?.getContactById(callback, contactId)

    private val callback = object : ResultListener {
        override fun onComplete(result: Contact) {
            view?.post(Runnable {
                val nameTextView = view?.findViewById<TextView>(R.id.contactNameDetails)
                val phoneNumTextView = view?.findViewById<TextView>(R.id.contactNum1)
                val phoneNum2TextView = view?.findViewById<TextView>(R.id.contactNum2)
                val emailTextView = view?.findViewById<TextView>(R.id.contactEmail1)
                val email2TextView = view?.findViewById<TextView>(R.id.contactEmail2)
                val descriptionTextView = view?.findViewById<TextView>(R.id.contactDescription)
                val imageView = view?.findViewById<ImageView>(R.id.contactImageDetails)

                nameTextView?.text = result.name
                phoneNumTextView?.text = result.firstNum
                phoneNum2TextView?.text = result.secondNum
                emailTextView?.text = result.firstEmail
                email2TextView?.text = result.secondEmail
                descriptionTextView?.text = result.description
                imageView?.setImageDrawable(activity?.let { ContextCompat.getDrawable(it.applicationContext, result.image) })
            })
        }
    }

    companion object {
        fun newInstance(id: Int): ContactDetailsFragment {
            val args = Bundle()
            args.putInt(EXTRA_CONTACT_ID, id)
            val fragment = ContactDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDetach() {
        serviceInterface = null
        super.onDetach()
    }

    interface ResultListener {
        fun onComplete(result: Contact)
    }
}