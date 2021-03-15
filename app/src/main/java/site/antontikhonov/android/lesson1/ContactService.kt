package site.antontikhonov.android.lesson1

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.lang.ref.WeakReference

class ContactService : Service() {
    private val binder = ContactBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun getContacts(callback: ContactListFragment.ResultListener) {
        val weakReferenceCallback = WeakReference(callback)
        Thread {
            weakReferenceCallback.get()?.onComplete(contacts)
        }.start()
    }

    fun getContactById(callback: ContactDetailsFragment.ResultListener, id: Int) {
        val weakReferenceCallback = WeakReference(callback)
        Thread {
            weakReferenceCallback.get()?.onComplete(contacts[id])
        }.start()
    }

    inner class ContactBinder : Binder() {
        fun getService(): ContactService = this@ContactService
    }

    interface ServiceInterface {
        fun getService(): ContactService?
    }
}