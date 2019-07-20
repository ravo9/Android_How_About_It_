package dreamcatcher.howaboutit.data.repositories

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import dreamcatcher.howaboutit.data.database.ItemEntity
import dreamcatcher.howaboutit.data.database.ItemsDatabaseInteractor
import dreamcatcher.howaboutit.network.ItemsNetworkInteractor

// Data Repository - the main gate of the model (data) part of the application
class ItemsRepository () {

    val databaseInteractor = ItemsDatabaseInteractor()
    val networkInteractor = ItemsNetworkInteractor()

    fun getSingleSavedItemById(id: String): LiveData<ItemEntity>? {
        return databaseInteractor.getSingleSavedItemById(id)
    }

    /*fun getAllItems(): LiveData<List<ItemEntity>>? {

        val returnList = MutableLiveData<List<ItemEntity>>()

        networkInteractor.getAllItems().subscribe {
            if (it.isSuccess) {

                val itemsSet = it.getOrNull()

                if (itemsSet != null) {
                    databaseInteractor.addItemsSet(itemsSet)
                    returnList.postValue(itemsSet)
                }
            }
        }

        return returnList
    }*/

    fun getAllItems(): LiveData<List<ItemEntity>>? {
        updateDataFromBackEnd()
        return databaseInteractor.getAllItems()
    }

    fun getConnectionEstablishedStatus(): LiveData<Boolean>? {
        return networkInteractor.connectionEstablishedStatus
    }

    fun getNetworkError(): LiveData<Boolean>? {
        return networkInteractor.networkError
    }

    @SuppressLint("CheckResult")
    private fun updateDataFromBackEnd() {
        networkInteractor.getAllItems().subscribe {
            if (it.isSuccess && it.getOrDefault(null)?.size!! > 0) {

                val itemsSet = it.getOrNull()

                // Clear database not to store outdated data, and save freshly fetched items
                if (itemsSet != null) {
                    databaseInteractor.addItemsSet(itemsSet)
                }
            }
        }
    }
}