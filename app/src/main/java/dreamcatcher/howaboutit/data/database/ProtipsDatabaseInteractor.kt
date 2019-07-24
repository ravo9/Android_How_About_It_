package dreamcatcher.howaboutit.data.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import dreamcatcher.howaboutit.general.HowAboutItApp
import dreamcatcher.howaboutit.network.ProtipPojo
import kotlinx.coroutines.launch

// Interactor used for communication between data repository and internal database
class ProtipsDatabaseInteractor() {

    var protipsDatabase: ProtipsDatabase? = null

    init {
        val context = HowAboutItApp.getAppContext()
        if (context != null) {
            protipsDatabase = Room.databaseBuilder(HowAboutItApp.getAppContext()!!, ProtipsDatabase::class.java, "protips_database").build()
        }
    }

    fun getSingleSaveProtipById(id: String): LiveData<ProtipEntity>? {
        return protipsDatabase?.getProtipsDao()?.getSingleSavedProtipById(id)
    }

    fun getAllProtips(): LiveData<List<ProtipEntity>>? {
        return protipsDatabase?.getProtipsDao()?.getAllSavedProtips()
    }

    // This function should be checked again.
    fun addProtipsSet(protipsSet: List<ProtipPojo>): LiveData<Boolean> {

        val protipSavingStatus = MutableLiveData<Boolean>()

        launch {
            protipsDatabase?.getProtipsDao()?.clearDatabase().also {
                protipsSet.forEach {
                    val protipEntity = ProtipEntity(
                        id = it.id,
                        protipText = it.protipText)
                    launch {
                        protipsDatabase?.getProtipsDao()?.insertNewProtip(protipEntity)
                    }
                }
            }.also {
                protipSavingStatus.postValue(true)
            }
        }
        return protipSavingStatus
    }
}