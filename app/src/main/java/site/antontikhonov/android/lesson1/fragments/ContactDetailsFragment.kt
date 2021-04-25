package site.antontikhonov.android.lesson1.fragments

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
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
import site.antontikhonov.android.lesson1.models.Contact
import site.antontikhonov.android.lesson1.ContactListApplication
import site.antontikhonov.android.lesson1.R
import site.antontikhonov.android.lesson1.extensions.injectViewModel
import site.antontikhonov.android.lesson1.receivers.BirthdayReceiver
import site.antontikhonov.android.lesson1.viewmodels.ContactDetailsViewModel
import java.lang.StringBuilder
import java.util.*
import javax.inject.Inject

const val EXTRA_CONTACT_ID = "CONTACT_ID"
const val EXTRA_NAME = "CONTACT_NAME"
const val EXTRA_START_CHECK_PERMISSION = "START_CHECK_PERMISSION"
const val URI_PACKAGE_SCHEME = "package:"

class ContactDetailsFragment : Fragment(R.layout.fragment_contact_details) {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var viewModel: ContactDetailsViewModel
    private var currentContact: Contact? = null
    private lateinit var contactId: String
    private var displayer: AlertDialogFragment.AlertDialogDisplayer? = null
    private var button: Button? = null
    private var isNotificationsEnabled = false
    private var progressBar: ProgressBar? = null
    private var contactObserver = Observer<Contact> {
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
        if (it.emailList != null) {
            firstEmailTextView.text = if(it.emailList.isNotEmpty()) it.emailList[0] else ""
            secondEmailTextView.text = if(it.emailList.size > 1) it.emailList[0] else ""
        }
        descriptionTextView.text = it.description
        if (it.dayOfBirthday != null && it.monthOfBirthday != null) {
            birthdayTextView.text = getString(
                R.string.contact_birthday,
                completeDateOfBirthday(it.dayOfBirthday, it.monthOfBirthday)
            )
            button?.isEnabled = true
        } else {
            birthdayTextView.text = ""
            button?.isEnabled = false
        }
        val photoUri: Uri? = it.image
        if (photoUri != null) {
            imageView.setImageURI(photoUri)
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
        if (context is AlertDialogFragment.AlertDialogDisplayer) {
            displayer = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (activity?.application as ContactListApplication)
            .appComponent
            .plusContactDetailsComponent()
            .inject(this)
        super.onCreate(savedInstanceState)
        viewModel = injectViewModel(factory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.contact_details_title)
        progressBar = view.findViewById(R.id.progress_bar_contact_details)
        contactId = requireNotNull(arguments?.getString(EXTRA_CONTACT_ID))
        button = view.findViewById(R.id.button_reminder)
        updateButtonState()
        button?.setOnClickListener { clickOnNotificationButton() }
        viewModel.contact.observe(viewLifecycleOwner, contactObserver)
        viewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            when(isLoading) {
                true -> progressBar?.visibility = View.VISIBLE
                false -> progressBar?.visibility = View.GONE
            }
        })
    }

    override fun onStart() {
        super.onStart()
        checkPermission()
    }

    override fun onDestroyView() {
        button = null
        progressBar = null
        super.onDestroyView()
    }

    override fun onDetach() {
        displayer = null
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

    private fun clickOnNotificationButton() {
        val pendingIntent = createPendingIntent()
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        if (isNotificationsEnabled) {
            button?.text = getString(R.string.on_notification)
            isNotificationsEnabled = false
            alarmManager?.cancel(pendingIntent)
            pendingIntent.cancel()
        } else {
            button?.text = getString(R.string.off_notification)
            isNotificationsEnabled = true
            alarmManager?.set(
                AlarmManager.RTC_WAKEUP,
                nextCalendarBirthday().timeInMillis,
                pendingIntent
            )
        }
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

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(activity, BirthdayReceiver::class.java)
            .putExtra(EXTRA_CONTACT_ID, contactId)
            .putExtra(EXTRA_NAME, currentContact?.name)
        return PendingIntent.getBroadcast(context, contactId.toInt(), intent, 0)
    }

    private fun isLeap(year: Int) = ((year % 4) == 0 && (year % 100) != 0) || (year % 400) == 0

    private fun loadContactById() = viewModel.getContactById(contactId)

    private fun nextCalendarBirthday(): Calendar {
        val dayBirthday: Int = requireNotNull(currentContact?.dayOfBirthday)
        val monthBirthday: Int = requireNotNull(currentContact?.monthOfBirthday?.minus(1))
        val birthdayCalendar = GregorianCalendar.getInstance()
        birthdayCalendar.set(Calendar.DAY_OF_MONTH, dayBirthday)
        birthdayCalendar.set(Calendar.MONTH, monthBirthday)
        val currentCalendar = GregorianCalendar.getInstance()
        if(birthdayCalendar.get(Calendar.MONTH) == Calendar.FEBRUARY && birthdayCalendar.get(Calendar.DAY_OF_MONTH) == 29) {
            if(isLeap(birthdayCalendar.get(Calendar.YEAR)) && birthdayCalendar.before(currentCalendar)) {
                birthdayCalendar.add(Calendar.YEAR, 4)
            } else if(!isLeap(birthdayCalendar.get(Calendar.YEAR))) {
                while(!isLeap(birthdayCalendar.get(Calendar.YEAR))) {
                    birthdayCalendar.add(Calendar.YEAR, 1)
                }
            }
        } else if (birthdayCalendar.before(currentCalendar)) {
            birthdayCalendar.add(Calendar.YEAR, 1)
        }
        return birthdayCalendar
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

    private fun updateButtonState() {
        isNotificationsEnabled = PendingIntent.getBroadcast(
            context,
            contactId.toInt(),
            Intent(activity, BirthdayReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE
        ) != null
        if (isNotificationsEnabled) {
            button?.text = getString(R.string.off_notification)
        } else {
            button?.text = getString(R.string.on_notification)
        }
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