package site.antontikhonov.android.lesson1

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar

class ContactListFragment : Fragment(R.layout.fragment_contact_list) {
    private var listener: ContactListListener? = null
    private var displayer: AlertDialogFragment.AlertDialogDisplayer? = null
    private var layout: View? = null
    private var contactTestId: String = "0"
    private var contactListObserver = Observer<List<Contact>> {
        contactTestId = it[0].id
        val nameTextView = requireView().findViewById<TextView>(R.id.contact_name)
        val phoneNumberTextView = requireView().findViewById<TextView>(R.id.contact_num)
        val imageView = requireView().findViewById<ImageView>(R.id.contact_image_details)
        nameTextView.text = it[0].name
        if (it[0].phoneList.isNotEmpty()) {
            phoneNumberTextView.text = it[0].phoneList[0]
        }
        val photoUri: Uri? = it[0].image
        if (photoUri != null) {
            imageView.setImageURI(photoUri)
        } else {
            imageView.setImageResource(R.drawable.contact)
        }
    }
    private var viewModel: ContactListViewModel? = null
    val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
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
        if (context is ContactListListener) {
            listener = context
        }
        if (context is AlertDialogFragment.AlertDialogDisplayer) {
            displayer = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactListViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.contact_list_title)
        layout = view.findViewById(R.id.contactListLayout)
        layout?.setOnClickListener { listener?.onClickFragment(contactTestId) }
    }

    override fun onStart() {
        super.onStart()
        checkPermission()
    }

    override fun onDestroyView() {
        layout = null
        super.onDestroyView()
    }

    override fun onDetach() {
        listener = null
        displayer = null
        requestPermissionLauncher.unregister()
        super.onDetach()
    }

    private fun checkPermission() {
        val isStartCheckPermission = arguments?.getBoolean(EXTRA_START_CHECK_PERMISSION) ?: true
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED -> {
                loadContacts()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                displayer?.displayAlertDialog(R.string.noPermissionsDialogList)
            }
            else -> {
                if (isStartCheckPermission) {
                    requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                }
            }
        }
    }

    private fun loadContacts() = viewModel?.getContactList(requireContext())
            ?.observe(viewLifecycleOwner, contactListObserver)

    private fun showNoContactPermissionSnackbar() {
        Snackbar.make(requireView(), R.string.snackbarTitleList, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.snackbarButton) {
                val appSettingsIntent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse(URI_PACKAGE_SCHEME + requireActivity().packageName)
                )
                startActivity(appSettingsIntent)
            }
            .show()
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

    interface ContactListListener {
        fun onClickFragment(id: String)
    }
}