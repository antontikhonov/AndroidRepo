package site.antontikhonov.android.lesson1

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

class ContactListFragment : Fragment() {

    private var listener: FragmentClickListener? = null
    private var layout: View? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is FragmentClickListener) {
            listener = context
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
        layout?.setOnClickListener{ listener?.onClickFragment() }
    }

    override fun onDestroyView() {
        layout = null
        super.onDestroyView()
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    interface FragmentClickListener {
        fun onClickFragment()
    }
}