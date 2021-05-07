package site.antontikhonov.android.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import site.antontikhonov.android.domain.contactdetails.ContactDetailsEntity
import site.antontikhonov.android.domain.contactdetails.ContactDetailsInteractor
import site.antontikhonov.android.domain.notification.BirthdayNotificationInteractor
import site.antontikhonov.android.presentation.schedulers.BaseSchedulerProvider
import site.antontikhonov.android.domain.contactlocation.ContactLocationEntity
import timber.log.Timber
import javax.inject.Inject

class ContactDetailsViewModel @Inject constructor(
       private val contactDetailsInteractor: ContactDetailsInteractor,
       private val birthdayNotificationInteractor: BirthdayNotificationInteractor,
       private val schedulerProvider: BaseSchedulerProvider
       )
       : ViewModel() {

       val contact: LiveData<ContactDetailsEntity>
              get() = mutableContact
       val isLoading: LiveData<Boolean>
              get() = mutableIsLoading
       val isSetNotification: LiveData<Boolean>
              get() = mutableIsSetNotification
       val location: LiveData<ContactLocationEntity>
              get() = mutableLocation
       private val mutableContact: MutableLiveData<ContactDetailsEntity> = MutableLiveData()
       private val mutableIsLoading: MutableLiveData<Boolean> = MutableLiveData()
       private val mutableIsSetNotification: MutableLiveData<Boolean> = MutableLiveData()
       private val mutableLocation: MutableLiveData<ContactLocationEntity> = MutableLiveData()
       private val disposable: CompositeDisposable = CompositeDisposable()

       override fun onCleared() {
              disposable.dispose()
              super.onCleared()
       }

       fun haveNotification(id: String)
              = mutableIsSetNotification.postValue(!birthdayNotificationInteractor.checkNotification(id))

       fun switchBirthdayNotification(contact: ContactDetailsEntity) {
              birthdayNotificationInteractor.switchBirthdayNotification(
                     contact.id,
                     contact.name,
                     contact.dayOfBirthday,
                     contact.monthOfBirthday - 1
              )
              haveNotification(contact.id)
       }

       fun getLocationById(id: String) {
              contactDetailsInteractor.loadLocationById(id)
                     .subscribeOn(schedulerProvider.io())
                     .observeOn(schedulerProvider.ui())
                     .doOnSubscribe { mutableIsLoading.postValue(true) }
                     .subscribeBy(
                            onSuccess = {
                                   mutableLocation.postValue(it)
                                   mutableIsLoading.postValue(false)
                            },
                            onError = {
                                   mutableIsLoading.postValue(false)
                                   Timber.e(it)
                            }
                     )
                     .addTo(disposable)
       }

       fun getContactById(id: String) {
              contactDetailsInteractor.loadContactById(id)
                     .subscribeOn(schedulerProvider.io())
                     .observeOn(schedulerProvider.ui())
                     .doOnSubscribe { mutableIsLoading.postValue(true) }
                     .subscribeBy(
                            onSuccess = {
                                   mutableContact.postValue(it)
                                   mutableIsLoading.postValue(false)
                            },
                            onError = {
                                   mutableIsLoading.postValue(false)
                                   Timber.e(it)
                            }
                     )
                     .addTo(disposable)
       }
}