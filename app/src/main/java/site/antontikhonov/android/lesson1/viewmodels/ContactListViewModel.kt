package site.antontikhonov.android.lesson1.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import site.antontikhonov.android.lesson1.models.Contact
import site.antontikhonov.android.lesson1.data.ContactRepository
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ContactListViewModel @Inject constructor(private val repository: ContactRepository)
       : ViewModel() {

       val contacts: LiveData<List<Contact>>
              get() = mutableContacts
       val isLoading: LiveData<Boolean>
              get() = mutableIsLoading
       private val disposable: CompositeDisposable = CompositeDisposable()
       private val mutableContacts: MutableLiveData<List<Contact>> = MutableLiveData()
       private val mutableIsLoading: MutableLiveData<Boolean> = MutableLiveData()

       override fun onCleared() {
              disposable.dispose()
              super.onCleared()
       }

       fun loadContactList() {
              repository.loadContactList("")
                     .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
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
                     .switchMapSingle { name -> repository.loadContactList(name)
                            .doOnSubscribe { mutableIsLoading.postValue(true) }
                     }
                     .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
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