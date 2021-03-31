package site.antontikhonov.android.lesson1

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import java.lang.ref.WeakReference

private const val TAG = "fragmentTag"
const val DIALOG_TAG = "dialogTag"

class MainActivity : AppCompatActivity(),
    ContactListFragment.ContactListListener,
    AlertDialogFragment.AlertDialogDisplayer,
    ContactService.ServiceInterface {

    private var contactService: ContactService? = null
    private var bound = false
    private var alertDialogFragment: AlertDialogFragment? = null
    private var connection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ContactService.ContactBinder
            contactService = binder.getService()
            bound = true
            val isStartCheckPermission: Boolean = intent.extras?.getBoolean(EXTRA_START_CHECK_PERMISSION) ?: true
            val weakReferenceFragment = WeakReference(supportFragmentManager.findFragmentByTag(TAG))
            when (val fragment = weakReferenceFragment.get()) {
                is ContactListFragment -> replaceContactListFragment(isStartCheckPermission)
                is ContactDetailsFragment -> replaceAndPopContactDetails(fragment.contactId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val isStartCheckPermission: Boolean = intent.extras?.getBoolean(EXTRA_START_CHECK_PERMISSION) ?: true
        if(!isStartCheckPermission && savedInstanceState == null) {
            replaceContactListFragment(isStartCheckPermission)
            val id = requireNotNull(intent?.extras?.getString(EXTRA_CONTACT_ID))
            replaceAndPopContactDetails(id)
        } else if(savedInstanceState == null) {
            replaceContactListFragment(false)
        }
        val intent = Intent(this, ContactService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if(alertDialogFragment?.isAdded == true) {
            alertDialogFragment?.dismissAllowingStateLoss()
        }
        super.onSaveInstanceState(outState)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val id = requireNotNull(intent?.extras?.getString(EXTRA_CONTACT_ID))
        replaceAndPopContactDetails(id)
    }

    override fun onDestroy() {
        if(bound) {
            unbindService(connection)
            bound = false
        }
        contactService = null
        super.onDestroy()
    }

    private fun replaceAndPopContactDetails(id: String) {
        val fragmentManager = supportFragmentManager
        if(fragmentManager.backStackEntryCount==1) {
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

    override fun onClickFragment(id: String) {
        replaceContactDetailsFragment(id)
    }

    override fun getService() : ContactService? {
        return contactService
    }

    override fun displayAlertDialog(resMessage: Int) {
        if(alertDialogFragment == null) {
            alertDialogFragment = AlertDialogFragment.newInstance(resMessage)
        }
        if(alertDialogFragment?.isAdded == false) {
            alertDialogFragment?.show(supportFragmentManager, DIALOG_TAG)
        }
    }

    fun restartCheckPermission() {
        val weakReferenceFragment = WeakReference(supportFragmentManager.findFragmentByTag(TAG))
        when (val fragment = weakReferenceFragment.get()) {
            is ContactListFragment -> fragment.requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            is ContactDetailsFragment -> fragment.requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }
}