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

class ContactListFragment : Fragment() {

    private var listener: ContactListListener? = null
    private var serviceInterface: ContactService.ServiceInterface? = null
    private var layout: View? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is ContactListListener) {
            listener = context
        }
        if(context is ContactService.ServiceInterface) {
            serviceInterface = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.contact_list_title)
        layout = view.findViewById(R.id.contactListLayout)
        layout?.setOnClickListener { listener?.onClickFragment() }
        loadContacts()
    }

    fun loadContacts() = serviceInterface?.getService()?.getContacts(callback)

    private val callback = object : ResultListener {
        override fun onComplete(result: ArrayList<Contact>) {
            view?.post(Runnable {
                val nameTextView = view?.findViewById<TextView>(R.id.contactNameDetails)
                val numTextView = view?.findViewById<TextView>(R.id.contactNum)
                val imageView = view?.findViewById<ImageView>(R.id.contactImageDetails)
                nameTextView?.text = result[0].name
                numTextView?.text = result[0].firstNum
                imageView?.setImageDrawable(activity?.let { ContextCompat.getDrawable(it.applicationContext, result[0].image) })
            })
        }

    }

    override fun onDestroyView() {
        layout = null
        super.onDestroyView()
    }

    override fun onDetach() {
        listener = null
        serviceInterface = null
        super.onDetach()
    }

    interface ContactListListener {
        fun onClickFragment()
    }

    interface ResultListener {
        fun onComplete(result: ArrayList<Contact>)
    }
}