package site.antontikhonov.android.presentation.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import site.antontikhonov.android.domain.contactdetails.ContactDetailsEntity
import site.antontikhonov.android.presentation.R
import site.antontikhonov.android.presentation.di.HasComponent
import site.antontikhonov.android.presentation.extensions.injectViewModel
import site.antontikhonov.android.presentation.viewmodels.ContactDetailsViewModel
import java.lang.StringBuilder
import java.util.Calendar
import javax.inject.Inject

const val EXTRA_CONTACT_ID = "CONTACT_ID"
const val EXTRA_NAME = "CONTACT_NAME"
const val EXTRA_MESSAGE = "CONTACT_MESSAGE"
const val EXTRA_START_CHECK_PERMISSION = "START_CHECK_PERMISSION"
const val URI_PACKAGE_SCHEME = "package:"

class ContactDetailsFragment : Fragment(R.layout.fragment_contact_details) {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var viewModel: ContactDetailsViewModel
    private var currentContact: ContactDetailsEntity? = null
    private lateinit var contactId: String
    private var displayer: AlertDialogFragment.AlertDialogDisplayer? = null
    private var buttonReminder: Button? = null
    private var buttonLocation: Button? = null
    private var progressBar: ProgressBar? = null
    private var onButtonLocationListener: OnButtonLocationListener? = null
    private var contactObserver = Observer<ContactDetailsEntity> {
        currentContact = it
        val nameTextView = requireView().findViewById<TextView>(R.id.contact_name_details)
        val firstPhoneNumberTextView = requireView().findViewById<TextView>(R.id.contact_num_first)
        val secondPhoneNumberTextView = requireView().findViewById<TextView>(R.id.contact_num_second)
        val firstEmailTextView = requireView().findViewById<TextView>(R.id.contact_email_first)
        val secondEmailTextView = requireView().findViewById<TextView>(R.id.contact_email_second)
        val descriptionTextView = requireView().findViewById<TextView>(R.id.contact_description)
        val birthdayTextView = requireView().findViewById<TextView>(R.id.contact_birthday)
        val imageView = requireView().findViewById<ImageView>(R.id.contact_image)
        nameTextView.text = it.name
        firstPhoneNumberTextView.text = if(it.phoneList.isNotEmpty()) it.phoneList[0] else ""
        secondPhoneNumberTextView.text = if(it.phoneList.size > 1) it.phoneList[1] else ""
        firstEmailTextView.text = if(it.emailList.isNotEmpty()) it.emailList[0] else ""
        secondEmailTextView.text = if(it.emailList.size > 1) it.emailList[0] else ""
        descriptionTextView.text = it.description
        if (it.dayOfBirthday != -1 && it.monthOfBirthday != -1) {
            birthdayTextView.text = getString(
                R.string.contact_birthday,
                completeDateOfBirthday(it.dayOfBirthday, it.monthOfBirthday)
            )
            buttonReminder?.isEnabled = true
        } else {
            birthdayTextView.text = ""
            buttonReminder?.isEnabled = false
        }
        if (it.image != null) {
            imageView.setImageURI(Uri.parse(it.image))
        } else {
            imageView.setImageResource(R.drawable.contact_placeholder)
        }
    }
    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                loadContactById()
            } else {
                when {
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                        displayer?.displayAlertDialog(R.string.no_permissions_dialog_details)
                    }
                    else -> {
                        showNoContactPermissionSnackbar()
                    }
                }
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is AlertDialogFragment.AlertDialogDisplayer) {
            displayer = context
        }
        if(context is OnButtonLocationListener) {
            onButtonLocationListener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as HasComponent)
            .getAppComponent()
            .plusContactDetailsContainer()
            .inject(this)
        super.onCreate(savedInstanceState)
        viewModel = injectViewModel(factory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.contact_details_title)
        progressBar = view.findViewById(R.id.progress_bar_contact_details)
        contactId = requireNotNull(arguments?.getString(EXTRA_CONTACT_ID))
        viewModel.haveNotification(contactId)
        buttonReminder = view.findViewById(R.id.button_reminder)
        buttonLocation = view.findViewById(R.id.button_location)
        buttonLocation?.setOnClickListener { clickOnLocationButton() }
        viewModel.contact.observe(viewLifecycleOwner, contactObserver)
        viewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            when(isLoading) {
                true -> progressBar?.visibility = View.VISIBLE
                false -> progressBar?.visibility = View.GONE
            }
        })
        viewModel.isSetNotification.observe(viewLifecycleOwner, this::updateButtonState)
        buttonReminder?.setOnClickListener {
            currentContact?.let { viewModel.switchBirthdayNotification(it) }
        }
        viewModel.location.observe(viewLifecycleOwner, { location ->
            buttonLocation?.text = getString(R.string.change_location)
            val addressTextView = view.findViewById<TextView>(R.id.contact_address)
            addressTextView.visibility = View.VISIBLE
            addressTextView.text = location.address
        })
    }

    override fun onStart() {
        super.onStart()
        checkPermission()
    }

    override fun onDestroyView() {
        buttonReminder = null
        progressBar = null
        super.onDestroyView()
    }

    override fun onDetach() {
        displayer = null
        onButtonLocationListener = null
        requestPermissionLauncher.unregister()
        super.onDetach()
    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED -> {
                loadContactById()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                displayer?.displayAlertDialog(R.string.no_permissions_dialog_details)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun clickOnLocationButton() {
        onButtonLocationListener?.onButtonLocationClick(contactId)
    }

    private fun completeDateOfBirthday(day: Int, month: Int): String {
        val result = StringBuilder("$day ")
        val monthArray = resources.getStringArray(R.array.array_months)
        when (month - 1) {
            Calendar.JANUARY -> result.append(monthArray[0])
            Calendar.FEBRUARY -> result.append(monthArray[1])
            Calendar.MARCH -> result.append(monthArray[2])
            Calendar.APRIL -> result.append(monthArray[3])
            Calendar.MAY -> result.append(monthArray[4])
            Calendar.JUNE -> result.append(monthArray[5])
            Calendar.JULY -> result.append(monthArray[6])
            Calendar.AUGUST -> result.append(monthArray[7])
            Calendar.SEPTEMBER -> result.append(monthArray[8])
            Calendar.OCTOBER -> result.append(monthArray[9])
            Calendar.NOVEMBER -> result.append(monthArray[10])
            Calendar.DECEMBER -> result.append(monthArray[11])
        }
        return result.toString()
    }

    private fun loadContactById() {
        viewModel.getContactById(contactId)
        viewModel.getLocationById(contactId)
    }

    private fun showNoContactPermissionSnackbar() {
        Snackbar.make(requireView(), R.string.snackbar_title_details, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.snackbar_button) {
                val appSettingsIntent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse(URI_PACKAGE_SCHEME + requireActivity().packageName)
                )
                startActivity(appSettingsIntent)
            }
            .show()
    }

    private fun updateButtonState(haveNotification: Boolean) {
        if(haveNotification) {
            buttonReminder?.text = getString(R.string.off_notification)
        } else {
            buttonReminder?.text = getString(R.string.on_notification)
        }
    }

    interface OnButtonLocationListener {
        fun onButtonLocationClick(id: String)
    }

    companion object {
        fun newInstance(id: String): ContactDetailsFragment {
            val args = Bundle()
            args.putString(EXTRA_CONTACT_ID, id)
            val fragment = ContactDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}