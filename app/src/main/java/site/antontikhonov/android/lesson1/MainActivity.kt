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
        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragments_container, ContactListFragment(), TAG)
                    .commit()
        }
        val intent = Intent(this, ContactService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        if(bound) {
            unbindService(connection)
            bound = false
        }
        contactService = null
        super.onDestroy()
    }

    override fun onClickFragment() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragments_container, ContactDetailsFragment.newInstance(CONTACT_ID), TAG)
                .addToBackStack(null)
                .commit()
    }

    override fun getService() : ContactService? {
        return contactService
    }
}