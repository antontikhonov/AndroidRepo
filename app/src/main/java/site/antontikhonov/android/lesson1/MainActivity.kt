package site.antontikhonov.android.lesson1

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.lang.ref.WeakReference

private const val TAG = "fragmentTag"
private const val DIALOG_TAG = "dialogTag"

class MainActivity : AppCompatActivity(),
    ContactListFragment.ContactListListener,
    AlertDialogFragment.AlertDialogDisplayer {

    private var alertDialogFragment: AlertDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
        val id = intent?.extras?.getString(EXTRA_CONTACT_ID)
        if(id != null && savedInstanceState == null) {
            replaceContactListFragment(false)
            popAndReplaceContactDetails(id)
        } else if(savedInstanceState == null) {
            replaceContactListFragment(true)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val id = requireNotNull(intent?.extras?.getString(EXTRA_CONTACT_ID))
        popAndReplaceContactDetails(id)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if(alertDialogFragment?.isAdded == true) {
            alertDialogFragment?.dismissAllowingStateLoss()
        }
        super.onSaveInstanceState(outState)
    }

    override fun displayAlertDialog(resMessage: Int) {
        if(alertDialogFragment == null) {
            alertDialogFragment = AlertDialogFragment.newInstance(resMessage)
        }
        if(alertDialogFragment?.isAdded == false) {
            alertDialogFragment?.show(supportFragmentManager, DIALOG_TAG)
        }
    }

    override fun onClickFragment(id: String) = replaceContactDetailsFragment(id)

    fun restartCheckPermission() {
        val weakReferenceFragment = WeakReference(supportFragmentManager.findFragmentByTag(TAG))
        when (val fragment = weakReferenceFragment.get()) {
            is ContactListFragment -> fragment.requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            is ContactDetailsFragment -> fragment.requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    private fun createNotificationChannel() {
        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, this.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun popAndReplaceContactDetails(id: String) {
        val fragmentManager = supportFragmentManager
        if(fragmentManager.backStackEntryCount == 1) {
            fragmentManager.popBackStack()
        }
        replaceContactDetailsFragment(id)
    }

    private fun replaceContactListFragment(isStartCheckPermission: Boolean) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragments_container, ContactListFragment.newInstance(isStartCheckPermission), TAG)
            .commit()
    }

    private fun replaceContactDetailsFragment(id: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragments_container, ContactDetailsFragment.newInstance(id), TAG)
            .addToBackStack(null)
            .commit()
    }
}