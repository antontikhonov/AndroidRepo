package site.antontikhonov.android.application

import android.app.Application
import site.antontikhonov.android.application.app.AppComponent
import site.antontikhonov.android.application.app.AppModule
import site.antontikhonov.android.application.app.DaggerAppComponent
import site.antontikhonov.android.presentation.di.AppContainer
import site.antontikhonov.android.presentation.di.HasComponent
import timber.log.Timber

class ContactListApplication : Application(), HasComponent {

    private val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun getAppComponent(): AppContainer = appComponent
}