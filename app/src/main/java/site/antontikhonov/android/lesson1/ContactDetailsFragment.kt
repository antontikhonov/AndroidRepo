package site.antontikhonov.android.lesson1

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
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import java.lang.StringBuilder
import java.util.*

const val EXTRA_CONTACT_ID = "CONTACT_ID"
const val EXTRA_NAME = "CONTACT_NAME"
const val EXTRA_START_CHECK_PERMISSION = "START_CHECK_PERMISSION"
const val URI_PACKAGE_SCHEME = "package:"

class ContactDetailsFragment : Fragment(R.layout.fragment_contact_details) {
    private var contactId: String = "0"
    private var displayer: AlertDialogFragment.AlertDialogDisplayer? = null
    private var currentContact: Contact? = null
    private var button: Button? = null
    private var isNotificationsEnabled = false
    private var viewModel: ContactDetailsViewModel? = null
    private var contactObserver = Observer<Contact> {
        currentContact = it
        if (it?.dayOfBirthday != null && it.monthOfBirthday != null) {
            button?.isEnabled = true
        }
        val nameTextView = requireView().findViewById<TextView>(R.id.contact_name_details)
        val firstPhoneNumberTextView = requireView().findViewById<TextView>(R.id.contact_num_first)
        val secondPhoneNumberTextView = requireView().findViewById<TextView>(R.id.contact_num_second)
        val firstEmailTextView = requireView().findViewById<TextView>(R.id.contact_email_first)
        val secondEmailTextView = requireView().findViewById<TextView>(R.id.contact_email_second)
        val descriptionTextView = requireView().findViewById<TextView>(R.id.contact_description)
        val birthdayTextView = requireView().findViewById<TextView>(R.id.contact_birthday)
        val imageView = requireView().findViewById<ImageView>(R.id.contact_image_details)
        nameTextView.text = it?.name
        if (it?.phoneList?.isNotEmpty() == true) {
            firstPhoneNumberTextView.text = it.phoneList[0]
            if (it.phoneList.size > 1) {
                secondPhoneNumberTextView.text = it.phoneList[1]
            }
        }
        if (it?.emailList != null) {
            if (it.emailList.isNotEmpty()) {
                firstEmailTextView.text = it.emailList[0]
            }
            if (it.emailList.size > 1) {
                secondEmailTextView.text = it.emailList[1]
            }
        }
        descriptionTextView.text = it?.description
        if (it?.dayOfBirthday != null && it.monthOfBirthday != null) {
            birthdayTextView.text = getString(
                    R.string.contact_birthday,
                    completeDateOfBirthday(it.dayOfBirthday, it.monthOfBirthday)
            )
        }
        val photoUri: Uri? = it?.image
        if (photoUri != null) {
            imageView.setImageURI(photoUri)
        } else {
            imageView.setImageResource(R.drawable.contact)
        }
    }
    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                loadContactById()
            } else {
                when {
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                        displayer?.displayAlertDialog(R.string.noPermissionsDialogDetails)
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
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactDetailsViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.contact_details_title)
        contactId = arguments?.getString(EXTRA_CONTACT_ID) ?: throw IllegalArgumentException("Contact ID required")
        button = view.findViewById(R.id.button_reminder)
        updateButtonState()
        button?.setOnClickListener { clickOnNotificationButton() }
    }

    override fun onStart() {
        super.onStart()
        checkPermission()
    }

    override fun onDestroyView() {
        button = null
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
                displayer?.displayAlertDialog(R.string.noPermissionsDialogDetails)
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

    private fun loadContactById() = viewModel?.getContactById(requireContext(), contactId)
            ?.observe(viewLifecycleOwner, contactObserver)

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
        Snackbar.make(requireView(), R.string.snackbarTitleDetails, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.snackbarButton) {
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