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

    // Temporary solution using hard-coded JSON database.
    /*fun getAllItems(): LiveData<List<ItemEntity>>? {
        val liveDataItemsList = MutableLiveData<List<ItemEntity>>()
        Gson().fromJson(HardCodedDatabase.hardCodedDatabase, JsonParserResult::class.java)
            .also {
                liveDataItemsList.value = it.itemsList
            }
        return liveDataItemsList
    }*/

    fun addNewItem(item: ItemPojo): LiveData<Boolean> {

        val itemSavingStatus = MutableLiveData<Boolean>()

        item.let {
            val itemEntity = ItemEntity(
                id = Integer.valueOf(it.id),
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
    }

    fun getSingleSavedItemById(id: String): LiveData<ItemEntity>? {
        return itemsDatabase?.getItemsDao()?.getSingleSavedItemById(id)
    }

    fun getAllItems(): LiveData<List<ItemEntity>>? {
        return itemsDatabase?.getItemsDao()?.getAllSavedItems()
    }

    fun clearDatabase() {
        launch {
            itemsDatabase?.getItemsDao()?.clearDatabase()
        }
    }
}



