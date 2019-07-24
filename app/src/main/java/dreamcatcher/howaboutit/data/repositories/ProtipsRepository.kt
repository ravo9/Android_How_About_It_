package dreamcatcher.howaboutit.data.repositories

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import dreamcatcher.howaboutit.data.database.ProtipEntity
import dreamcatcher.howaboutit.data.database.ProtipsDatabaseInteractor
import dreamcatcher.howaboutit.network.ItemsNetworkInteractor

// Data Repository - the main gate of the model (data) part of the application
class ProtipsRepository () {

    private val databaseInteractor = ProtipsDatabaseInteractor()
    private val networkInteractor = ItemsNetworkInteractor()

    fun getAllProtips(): LiveData<List<ProtipEntity>>? {
        updateDataFromBackEnd()
        return databaseInteractor.getAllProtips()
    }

    /*fun getConnectionEstablishedStatus(): LiveData<Boolean>? {
        return networkInteractor.connectionEstablishedStatus
    }

    fun getNetworkError(): LiveData<Boolean>? {
        return networkInteractor.networkError
    }*/

    @SuppressLint("CheckResult")
    private fun updateDataFromBackEnd() {

        // Fetch protips
        networkInteractor.getAllProtips().subscribe {
            if (it.isSuccess && it.getOrDefault(null)?.size!! > 0) {

                val protipsSet = it.getOrNull()

                // Clear database not to store outdated data, and save freshly fetched items
                if (protipsSet != null) {
                    databaseInteractor.addProtipsSet(protipsSet)
                }
            }
        }
    }
}