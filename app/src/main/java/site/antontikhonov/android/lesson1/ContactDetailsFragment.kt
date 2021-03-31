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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.lang.StringBuilder
import java.util.*

const val EXTRA_CONTACT_ID = "CONTACT_ID"
const val EXTRA_NAME = "CONTACT_NAME"
const val EXTRA_START_CHECK_PERMISSION = "START_CHECK_PERMISSION"
const val uriPackageScheme = "package:"

class ContactDetailsFragment : Fragment() {
    var contactId: String = "0"
    private var serviceInterface: ContactService.ServiceInterface? = null
    private var displayer: AlertDialogFragment.AlertDialogDisplayer? = null
    private var currentContact: Contact? = null
    private lateinit var button: Button
    private var isNotificationsEnabled = false
    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
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
        return inflater.inflate(R.layout.fragment_contact_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.contact_details_title)
        contactId = arguments?.getString(EXTRA_CONTACT_ID) ?: throw IllegalArgumentException("Contact ID required")
        button = view.findViewById(R.id.button_reminder)

        updateButtonState()
        button.setOnClickListener { clickOnNotificationButton() }
    }

    override fun onStart() {
        super.onStart()
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED -> {
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

    private fun showNoContactPermissionSnackbar() {
        Snackbar.make(requireView(), R.string.snackbarTitleDetails, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.snackbarButton) {
                val appSettingsIntent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse(uriPackageScheme + requireActivity().packageName)
                )
                startActivity(appSettingsIntent)
            }
            .show()
    }

    private fun clickOnNotificationButton() {
        val pendingIntent = createPendingIntent()
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        if(isNotificationsEnabled) {
            button.text = getString(R.string.on_notification)
            isNotificationsEnabled = false
            alarmManager?.cancel(pendingIntent)
            pendingIntent.cancel()
        } else {
            button.text = getString(R.string.off_notification)
            isNotificationsEnabled = true
            alarmManager?.set(AlarmManager.RTC_WAKEUP, nextCalendarBirthday().timeInMillis, pendingIntent)
        }
    }

    private fun updateButtonState() {
        isNotificationsEnabled = PendingIntent.getBroadcast(context, contactId.toInt(), Intent(activity, MyReceiver::class.java), PendingIntent.FLAG_NO_CREATE) != null
        if(isNotificationsEnabled) {
            button.text = getString(R.string.off_notification)
        } else {
            button.text = getString(R.string.on_notification)
        }
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(activity, MyReceiver::class.java)
        intent.putExtra(EXTRA_CONTACT_ID, contactId)
        intent.putExtra(EXTRA_NAME, currentContact?.name)
        return PendingIntent.getBroadcast(context, contactId.toInt(), intent, 0)
    }

    private fun nextCalendarBirthday(): Calendar {
        val currentCalendar = GregorianCalendar.getInstance()
        val birthdayCalendar = GregorianCalendar.getInstance()
        val dayBirthday: Int = requireNotNull(currentContact?.dayOfBirthday)
        val monthBirthday: Int = requireNotNull(currentContact?.monthOfBirthday?.minus(1))
        birthdayCalendar.set(Calendar.DAY_OF_MONTH, dayBirthday)
        birthdayCalendar.set(Calendar.MONTH, monthBirthday)
        if(birthdayCalendar.before(currentCalendar)) {
            birthdayCalendar.add(Calendar.YEAR, 1)
        }
        return birthdayCalendar
    }

    private fun loadContactById() = serviceInterface?.getService()?.getContactById(callback, contactId)

    private val callback = object : ResultListener {
        override fun onComplete(result: Contact?) {
            view?.post {
                currentContact = result
                if (result?.dayOfBirthday != null && result.monthOfBirthday != null) {
                    button.isEnabled = true
                }
                val nameTextView = view?.findViewById<TextView>(R.id.contact_name_details)
                val phoneNumTextView = view?.findViewById<TextView>(R.id.contact_num_first)
                val phoneNum2TextView = view?.findViewById<TextView>(R.id.contact_num_second)
                val emailTextView = view?.findViewById<TextView>(R.id.contact_email_first)
                val email2TextView = view?.findViewById<TextView>(R.id.contact_email_second)
                val descriptionTextView = view?.findViewById<TextView>(R.id.contact_description)
                val birthdayTextView = view?.findViewById<TextView>(R.id.contact_birthday)
                val imageView = view?.findViewById<ImageView>(R.id.contact_image_details)

                nameTextView?.text = result?.name
                if (result?.phoneList?.isNotEmpty() == true) {
                    phoneNumTextView?.text = result.phoneList[0]
                    if (result.phoneList.size > 1) {
                        phoneNum2TextView?.text = result.phoneList[1]
                    }
                }
                if (result?.emailList != null) {
                    if (result.emailList.isNotEmpty()) {
                        emailTextView?.text = result.emailList[0]
                    }
                    if (result.emailList.size > 1) {
                        email2TextView?.text = result.emailList[1]
                    }
                }
                descriptionTextView?.text = result?.description
                if (result?.dayOfBirthday != null && result.monthOfBirthday != null) {
                    birthdayTextView?.text = getString(
                        R.string.contact_birthday,
                        completeDateOfBirthday(result.dayOfBirthday, result.monthOfBirthday)
                    )
                }
                val photoUri: Uri? = result?.image
                if (photoUri != null) {
                    imageView?.setImageURI(photoUri)
                } else {
                    imageView?.setImageResource(R.drawable.contact)
                }
            }
        }
    }

    private fun completeDateOfBirthday(day: Int, month: Int): String {
        val result = StringBuilder("$day ")
        val monthArray = resources.getStringArray(R.array.array_months)
        when(month-1) {
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

    companion object {
        fun newInstance(id: String): ContactDetailsFragment {
            val args = Bundle()
            args.putString(EXTRA_CONTACT_ID, id)
            val fragment = ContactDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDetach() {
        serviceInterface = null
        displayer = null
        requestPermissionLauncher.unregister()
        super.onDetach()
    }

    interface ResultListener {
        fun onComplete(result: Contact?)
    }
}