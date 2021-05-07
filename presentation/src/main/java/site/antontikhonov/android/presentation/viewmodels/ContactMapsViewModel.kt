package site.antontikhonov.android.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import site.antontikhonov.android.domain.contactlocation.ContactLocationEntity
import site.antontikhonov.android.domain.contactlocation.ContactLocationInteractor
import timber.log.Timber
import javax.inject.Inject

class ContactMapsViewModel @Inject constructor(private val interactor: ContactLocationInteractor)
    : ViewModel() {

    val location: LiveData<ContactLocationEntity>
        get() = mutableLocation
    val allLocations: LiveData<List<ContactLocationEntity>>
        get() = mutableAllLocations
    val isLoading: LiveData<Boolean>
        get() = mutableIsLoading
    private val mutableLocation: MutableLiveData<ContactLocationEntity> = MutableLiveData()
    private val mutableAllLocations: MutableLiveData<List<ContactLocationEntity>> = MutableLiveData()
    private val mutableIsLoading: MutableLiveData<Boolean> = MutableLiveData()
    private val disposable: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    fun getLocationById(id: String) {
        interactor.loadLocationById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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

    fun getLocations() {
        interactor.loadLocations()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mutableIsLoading.postValue(true) }
            .subscribeBy(
                onSuccess = {
                    mutableAllLocations.postValue(it)
                    mutableIsLoading.postValue(false)
                },
                onError = {
                    mutableIsLoading.postValue(false)
                    Timber.e(it)
                }
            )
            .addTo(disposable)
    }

    fun addLocation(contactLocationEntity: ContactLocationEntity) {
        interactor.addContactLocation(contactLocationEntity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mutableIsLoading.postValue(true) }
            .subscribeBy(
                onSuccess = {
                    mutableIsLoading.postValue(false)
                },
                onError = {
                    mutableIsLoading.postValue(false)
                    Timber.e(it)
                }
            )
            .addTo(disposable)
    }

    fun updateLocation(contactLocationEntity: ContactLocationEntity) {
        interactor.updateContactLocation(contactLocationEntity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mutableIsLoading.postValue(true) }
            .subscribeBy(
                onSuccess = {
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