package dreamcatcher.howaboutit.data.repositories

import androidx.lifecycle.LiveData
import dreamcatcher.howaboutit.data.database.ItemEntity
import dreamcatcher.howaboutit.data.database.ItemsDatabaseInteractor

// Data Repository - the main gate of the model (data) part of the application
class ItemsRepository (private val databaseInteractor: ItemsDatabaseInteractor) {

    /*fun getSingleSavedItemById(id: String): LiveData<ItemEntity>? {
        return databaseInteractor.getSingleSavedItemById(id)
    }

    fun getAllItems(): LiveData<List<ItemEntity>>? {
        //updateDataFromBackEnd()
        return databaseInteractor.getAllItems()
    }*/

    /*fun getNetworkError(): LiveData<Boolean>? {
        return itemsNetworkInteractor.networkError
    }*/

    /*@SuppressLint("CheckResult")
    private fun updateDataFromBackEnd() {
        itemsNetworkInteractor.getAllItems().subscribe {
            if (it.isSuccess && it.getOrDefault(null)?.size!! > 0) {

                // Clear database not to store outdated data
                databaseInteractor.clearDatabase()

                // Save freshly fetched items
                it.getOrNull()?.forEach {
                    databaseInteractor.addNewItem(it)
                }
            }
        }
    }*/
}