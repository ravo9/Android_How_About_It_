package dreamcatcher.howaboutit.data.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import dreamcatcher.howaboutit.general.HowAboutItApp
import dreamcatcher.howaboutit.network.ItemPojo
import kotlinx.coroutines.launch

// Interactor used for communication between data repository and internal database
class ItemsDatabaseInteractor() {

    var itemsDatabase: ItemsDatabase? = null

    init {
        val context = HowAboutItApp.getAppContext()
        if (context != null) {
            itemsDatabase = Room.databaseBuilder(HowAboutItApp.getAppContext()!!, ItemsDatabase::class.java, "items_database").build()
        }
    }

    /*fun addNewItem(item: ItemPojo): LiveData<Boolean> {

        val itemSavingStatus = MutableLiveData<Boolean>()

        item.let {
            val itemEntity = ItemEntity(
                id = it.id,
                name = it.name,
                tags = it.tags,
                recyclingSteps = it.recyclingSteps,
                imageLink = it.imageLink)
            launch {
                itemsDatabase?.getItemsDao()?.insertNewItem(itemEntity)
            }
        }
        itemSavingStatus.postValue(true)
        return itemSavingStatus
    }*/

    fun getSingleSavedItemById(id: String): LiveData<ItemEntity>? {
        return itemsDatabase?.getItemsDao()?.getSingleSavedItemById(id)
    }

    fun getAllItems(): LiveData<List<ItemEntity>>? {
        return itemsDatabase?.getItemsDao()?.getAllSavedItems()
    }

    /*fun clearDatabase() {
        itemsDatabase?.getItemsDao()?.clearDatabase()
    }*/

    // This function should be checked again.
    fun addItemsSet(itemsSet: List<ItemPojo>): LiveData<Boolean> {

        val itemSavingStatus = MutableLiveData<Boolean>()

        launch {
            itemsDatabase?.getItemsDao()?.clearDatabase().also {
                itemsSet.forEach {
                    val itemEntity = ItemEntity(
                        id = it.id,
                        name = it.name,
                        tags = it.tags,
                        recyclingSteps = it.recyclingSteps,
                        imageLink = it.imageLink)
                    launch {
                        itemsDatabase?.getItemsDao()?.insertNewItem(itemEntity)
                    }
                }
            }.also {
                itemSavingStatus.postValue(true)
            }
        }
        return itemSavingStatus
    }
}



