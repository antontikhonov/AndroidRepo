package site.antontikhonov.android.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import site.antontikhonov.android.domain.contactlist.ContactListEntity
import site.antontikhonov.android.domain.contactlist.ContactListInteractor
import site.antontikhonov.android.presentation.schedulers.BaseSchedulerProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ContactListViewModel @Inject constructor(
       private val interactor: ContactListInteractor,
       private val schedulerProvider: BaseSchedulerProvider
       )
       : ViewModel() {

       val contacts: LiveData<List<ContactListEntity>>
              get() = mutableContacts
       val isLoading: LiveData<Boolean>
              get() = mutableIsLoading
       private val disposable: CompositeDisposable = CompositeDisposable()
       private val mutableContacts: MutableLiveData<List<ContactListEntity>> = MutableLiveData()
       private val mutableIsLoading: MutableLiveData<Boolean> = MutableLiveData()

       override fun onCleared() {
              disposable.dispose()
              super.onCleared()
       }

       fun loadContactList() {
              interactor.loadContacts("")
                     .subscribeOn(schedulerProvider.io())
                     .observeOn(schedulerProvider.ui())
                     .doOnSubscribe { mutableIsLoading.postValue(true) }
                     .subscribeBy(
                            onSuccess = {
                                   mutableContacts.postValue(it)
                                   mutableIsLoading.postValue(false)
                            },
                            onError = {
                                   mutableIsLoading.postValue(false)
                                   Timber.e(it)
                            }
                     )
                     .addTo(disposable)
       }

       fun searchContact(subject: PublishSubject<String>) {
              subject.debounce(300, TimeUnit.MILLISECONDS)
                     .distinctUntilChanged()
                     .switchMapSingle { name -> interactor.loadContacts(name)
                            .doOnSubscribe { mutableIsLoading.postValue(true) }
                     }
                     .subscribeOn(schedulerProvider.io())
                     .observeOn(schedulerProvider.ui())
                     .subscribeBy(
                            onNext = {
                                   mutableContacts.postValue(it)
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