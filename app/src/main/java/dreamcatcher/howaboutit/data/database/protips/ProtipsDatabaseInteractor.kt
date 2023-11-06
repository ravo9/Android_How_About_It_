package dreamcatcher.howaboutit.data.database.protips

import androidx.lifecycle.LiveData
import androidx.room.Room
import dreamcatcher.howaboutit.general.HowAboutItApp
import dreamcatcher.howaboutit.network.ProtipPojo
import io.reactivex.Observable
import io.reactivex.subjects.SingleSubject
import kotlinx.coroutines.GlobalScope
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

    fun getAllProtips(): LiveData<List<ProtipEntity>>? {
        return protipsDatabase?.getProtipsDao()?.getAllSavedProtips()
    }

    // This function should be checked again.
    fun addProtipsSet(protipsSet: List<ProtipPojo>): Observable<Result<Boolean>> {

        val dataUpdateFinishedStatus = SingleSubject.create<Result<Boolean>>()

        GlobalScope.launch {
            protipsDatabase?.getProtipsDao()?.clearDatabase().also {
                protipsSet.forEach {
                    val protipEntity = ProtipEntity(
                        id = it.id,
                        protipText = it.protipText
                    )
                    launch {
                        protipsDatabase?.getProtipsDao()?.insertNewProtip(protipEntity)
                    }
                }

            }.also {
                dataUpdateFinishedStatus.onSuccess(Result.success(true))
            }
        }

        return dataUpdateFinishedStatus.toObservable()
    }
}