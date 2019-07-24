package dreamcatcher.howaboutit.features.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dreamcatcher.howaboutit.data.database.ItemEntity
import dreamcatcher.howaboutit.data.database.ItemsDatabaseInteractor
import dreamcatcher.howaboutit.data.database.ProtipEntity
import dreamcatcher.howaboutit.data.repositories.ItemsRepository
import dreamcatcher.howaboutit.data.repositories.ProtipsRepository

class FeedViewModel : ViewModel() {

    private val itemsRepository = ItemsRepository()
    private val protipsRepository = ProtipsRepository()

    fun getAllItems(): LiveData<List<ItemEntity>>? {
        return itemsRepository.getAllItems()
    }

    fun getAllProtips(): LiveData<List<ProtipEntity>>? {
        return protipsRepository.getAllProtips()
    }

    fun getConnectionEstablishedStatus(): LiveData<Boolean>? {
        return itemsRepository.getConnectionEstablishedStatus()
    }

    fun getNetworkError(): LiveData<Boolean>? {
        return itemsRepository.getNetworkError()
    }
}