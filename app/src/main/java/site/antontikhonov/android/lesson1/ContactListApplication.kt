package site.antontikhonov.android.lesson1

import android.app.Application
import site.antontikhonov.android.lesson1.di.components.AppComponent
import site.antontikhonov.android.lesson1.di.components.DaggerAppComponent
import site.antontikhonov.android.lesson1.di.modules.AppModule
import timber.log.Timber

class ContactListApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}