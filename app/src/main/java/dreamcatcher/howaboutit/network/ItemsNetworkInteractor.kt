package dreamcatcher.howaboutit.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.SingleSubject

// Interactor used for communication between data repository and the external API
class ItemsNetworkInteractor {

    private val apiClient = NetworkAdapter.apiClient()
    val networkError: MutableLiveData<Boolean> = MutableLiveData()

    fun getAllItems(): Observable<Result<List<ItemPojo>>> {
        val allItemsSubject = SingleSubject.create<Result<List<ItemPojo>>>()

        apiClient.getItems()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it != null) {
                        allItemsSubject.onSuccess(Result.success(it!!))
                        //connectionEstablishedStatus.postValue(true)
                    } else {
                        Log.e("getItems() error: ", "NULL value")
                    }
                },
                {
                    networkError.postValue(true)
                    Log.e("getItems() error: ", it.message)
                })

        return allItemsSubject.toObservable()
    }

    fun getAllProtips(): Observable<Result<List<ProtipPojo>>> {
        val allProtipsSubject = SingleSubject.create<Result<List<ProtipPojo>>>()

        apiClient.getProtips()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it != null) {
                        allProtipsSubject.onSuccess(Result.success(it!!))
                        //connectionEstablishedStatus.postValue(true)
                    } else {
                        Log.e("getItems() error: ", "NULL value")
                    }
                },
                {
                    networkError.postValue(true)
                    Log.e("getItems() error: ", it.message)
                })

        return allProtipsSubject.toObservable()
    }
}