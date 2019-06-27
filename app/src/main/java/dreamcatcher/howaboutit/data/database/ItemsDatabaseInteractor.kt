package dreamcatcher.howaboutit.data.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch

// Interactor used for communication between data repository and internal database
class ItemsDatabaseInteractor(private val itemsDatabase: ItemsDatabase) {

    /*fun addNewItem(item: ApiResponse.Item?): LiveData<Boolean> {

        val itemSavingStatus = MutableLiveData<Boolean>()

        item?.let {
            val itemEntity = ItemEntity(
                backEndId = it.id,
                name = it.name,
                price = it.price,
                color = it.color,
                image = it.image)
            launch {
                itemsDatabase.getItemsDao().insertNewItem(itemEntity)
            }
        }
        itemSavingStatus.postValue(true)
        return itemSavingStatus
    }*/

    fun getSingleSavedItemById(id: String): LiveData<ItemEntity>? {
        return itemsDatabase.getItemsDao().getSingleSavedItemById(id)
    }

    fun getAllItems(): LiveData<List<ItemEntity>>? {
        return itemsDatabase.getItemsDao().getAllSavedItems()
    }

    fun clearDatabase() {
        launch {
            itemsDatabase.getItemsDao().clearDatabase()
        }
    }
}



