package dreamcatcher.howaboutit.data.repositories

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dreamcatcher.howaboutit.data.database.protips.ProtipEntity
import dreamcatcher.howaboutit.data.database.protips.ProtipsDatabaseInteractor
import dreamcatcher.howaboutit.network.ItemsNetworkInteractor

// Data Repository - the main gate of the model (data) part of the application
class ProtipsRepository () {

    private val databaseInteractor = ProtipsDatabaseInteractor()
    private val networkInteractor = ItemsNetworkInteractor()

    fun updateDatabaseWithServer(): LiveData<Boolean>? {
        return updateDataFromBackEnd()
    }

    fun getAllProtips(): LiveData<List<ProtipEntity>>? {
        return databaseInteractor.getAllProtips()
    }

    /*fun getNetworkError(): LiveData<Boolean>? {
        return networkInteractor.networkError
    }*/

    @SuppressLint("CheckResult")
    private fun updateDataFromBackEnd(): LiveData<Boolean>? {

        val dataUpdateFinishedStatus = MutableLiveData<Boolean>()

        // Fetch protips
        networkInteractor.getAllProtips().subscribe {
            if (it.isSuccess && it.getOrDefault(null)?.size!! > 0) {

                val protipsSet = it.getOrNull()

                // Clear database not to store outdated data, and save freshly fetched items
                if (protipsSet != null) {

                    databaseInteractor.addProtipsSet(protipsSet).subscribe{
                        dataUpdateFinishedStatus.postValue(true)
                    }
                }
            }
        }

        return dataUpdateFinishedStatus
    }
}