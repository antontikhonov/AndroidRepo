package site.antontikhonov.android.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import site.antontikhonov.android.domain.contactdetails.ContactDetailsEntity
import site.antontikhonov.android.domain.contactdetails.ContactDetailsInteractor
import timber.log.Timber
import javax.inject.Inject

class ContactDetailsViewModel @Inject constructor(private val interactor: ContactDetailsInteractor)
       : ViewModel() {

       val contact: LiveData<ContactDetailsEntity>
              get() = mutableContact
       val isLoading: LiveData<Boolean>
              get() = mutableIsLoading
       private val disposable: CompositeDisposable = CompositeDisposable()
       private val mutableContact: MutableLiveData<ContactDetailsEntity> = MutableLiveData()
       private val mutableIsLoading: MutableLiveData<Boolean> = MutableLiveData()

       override fun onCleared() {
              disposable.dispose()
              super.onCleared()
       }

       fun getContactById(id: String) {
              interactor.loadContactById(id)
                     .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
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