package site.antontikhonov.android.lesson1

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import java.lang.ref.WeakReference

private const val CONTACT_ID = 0
private const val TAG = "fragmentTag"

class MainActivity : AppCompatActivity(),
    ContactListFragment.ContactListListener,
    ContactService.ServiceInterface {

    private var contactService: ContactService? = null
    private var bound = false

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ContactService.ContactBinder
            contactService = binder.getService()
            bound = true
            val weakReferenceFragment = WeakReference(supportFragmentManager.findFragmentByTag(TAG))
            when (val fragment = weakReferenceFragment.get()) {
                is ContactListFragment -> fragment.loadContacts()
                is ContactDetailsFragment -> fragment.loadContactById()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(intent.extras?.containsKey(EXTRA_CONTACT_ID) == true && savedInstanceState == null) {
            addContactListFragment()
            startContactDetailsFromNotification(intent)
        } else if(savedInstanceState == null) {
            addContactListFragment()
        }
        val intent = Intent(this, ContactService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        startContactDetailsFromNotification(intent)
    }

    override fun onDestroy() {
        if(bound) {
            unbindService(connection)
            bound = false
        }
        contactService = null
        super.onDestroy()
    }

    private fun startContactDetailsFromNotification(intent: Intent?) {
        val fragmentManager = supportFragmentManager
        if(fragmentManager.backStackEntryCount==1) {
            fragmentManager.popBackStack()
        }
        val id: Int = requireNotNull(intent?.extras?.getInt(EXTRA_CONTACT_ID))
        replaceContactDetailsFragment(id)
    }

    private fun addContactListFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragments_container, ContactListFragment(), TAG)
            .commit()
    }

    private fun replaceContactDetailsFragment(id: Int) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragments_container, ContactDetailsFragment.newInstance(id), TAG)
            .addToBackStack(null)
            .commit()
    }

    override fun onClickFragment() {
        replaceContactDetailsFragment(CONTACT_ID)
    }

    override fun getService() : ContactService? {
        return contactService
    }
}