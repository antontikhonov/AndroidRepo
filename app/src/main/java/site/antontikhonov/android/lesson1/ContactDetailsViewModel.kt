package site.antontikhonov.android.lesson1

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class ContactDetailsViewModel : ViewModel() {

       val contact: LiveData<Contact>
              get() = mutableContact
       val isLoading: LiveData<Boolean>
              get() = mutableIsLoading

       private val disposable: CompositeDisposable = CompositeDisposable()
       private val mutableContact: MutableLiveData<Contact> = MutableLiveData()
       private val mutableIsLoading: MutableLiveData<Boolean> = MutableLiveData()

       override fun onCleared() {
              disposable.dispose()
              super.onCleared()
       }

       fun getContactById(context: Context, id: String) {
              ContactProviderRepository.loadContact(context, id)
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