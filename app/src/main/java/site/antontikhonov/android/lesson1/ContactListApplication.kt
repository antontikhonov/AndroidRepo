package site.antontikhonov.android.lesson1

import android.app.Application
import timber.log.Timber

class ContactListApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}