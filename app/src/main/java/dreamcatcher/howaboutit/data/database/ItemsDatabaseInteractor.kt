package dreamcatcher.howaboutit.data.database

import androidx.lifecycle.LiveData
import androidx.room.Room
import dreamcatcher.howaboutit.general.HowAboutItApp
import dreamcatcher.howaboutit.network.ItemPojo
import io.reactivex.Observable
import io.reactivex.subjects.SingleSubject
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

    fun getSingleSavedItemById(id: String): LiveData<ItemEntity>? {
        return itemsDatabase?.getItemsDao()?.getSingleSavedItemById(id)
    }

    fun getAllItems(): LiveData<List<ItemEntity>>? {
        return itemsDatabase?.getItemsDao()?.getAllSavedItems()
    }

    // This function should be checked again.
    fun addItemsSet(itemsSet: List<ItemPojo>): Observable<Result<Boolean>> {

        val dataUpdateFinishedStatus = SingleSubject.create<Result<Boolean>>()

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
                dataUpdateFinishedStatus.onSuccess(Result.success(true))
            }
        }

        return dataUpdateFinishedStatus.toObservable()
    }
}