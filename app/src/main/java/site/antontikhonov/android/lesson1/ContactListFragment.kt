package site.antontikhonov.android.lesson1

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

class ContactListFragment : Fragment() {
    private var listener: ContactListListener? = null
    private var displayer: AlertDialogFragment.AlertDialogDisplayer? = null
    private var serviceInterface: ContactService.ServiceInterface? = null
    private var layout: View? = null
    private var isStartCheckPermission = true
    private var contactTestId: String = "0"
    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            loadContacts()
        } else {
            when {
                shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                    displayer?.displayAlertDialog(R.string.noPermissionsDialogList)
                }
                else -> {
                    showNoContactPermissionSnackbar()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is ContactListListener) {
            listener = context
        }
        if(context is ContactService.ServiceInterface) {
            serviceInterface = context
        }
        if(context is AlertDialogFragment.AlertDialogDisplayer) {
            displayer = context
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
        layout?.setOnClickListener { listener?.onClickFragment(contactTestId) }
    }

    override fun onStart() {
        super.onStart()
        isStartCheckPermission = arguments?.getBoolean(EXTRA_START_CHECK_PERMISSION) ?: true
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED -> {
                loadContacts()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                displayer?.displayAlertDialog(R.string.noPermissionsDialogList)
            }
            else -> {
                if(isStartCheckPermission) {
                    requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                }
            }
        }
    }

    private fun showNoContactPermissionSnackbar() {
        Snackbar.make(requireView(), R.string.snackbarTitleList, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.snackbarButton) {
                val appSettingsIntent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse(uriPackageScheme + requireActivity().packageName)
                )
                startActivity(appSettingsIntent)
            }
            .show()
    }

    private fun loadContacts() = serviceInterface?.getService()?.getContacts(callback)

    private val callback = object : ResultListener {
        override fun onComplete(result: List<Contact>) {
            view?.post {
                contactTestId = result[0].id
                val nameTextView = view?.findViewById<TextView>(R.id.contact_name)
                val numTextView = view?.findViewById<TextView>(R.id.contact_num)
                val imageView = view?.findViewById<ImageView>(R.id.contact_image_details)
                nameTextView?.text = result[0].name
                if (result[0].phoneList.isNotEmpty()) {
                    numTextView?.text = result[0].phoneList[0]
                }
                val photoUri: Uri? = result[0].image
                if (photoUri != null) {
                    imageView?.setImageURI(photoUri)
                } else {
                    imageView?.setImageResource(R.drawable.contact)
                }
            }
        }
    }

    companion object {
        fun newInstance(isStartCheckPermission: Boolean): ContactListFragment {
            val args = Bundle()
            args.putBoolean(EXTRA_START_CHECK_PERMISSION, isStartCheckPermission)
            val fragment = ContactListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroyView() {
        layout = null
        super.onDestroyView()
    }

    override fun onDetach() {
        listener = null
        displayer = null
        serviceInterface = null
        requestPermissionLauncher.unregister()
        super.onDetach()
    }

    interface ContactListListener {
        fun onClickFragment(id: String)
    }

    interface ResultListener {
        fun onComplete(result: List<Contact>)
    }
}