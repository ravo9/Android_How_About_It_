package dreamcatcher.howaboutit.features.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dreamcatcher.howaboutit.data.database.ItemEntity
import dreamcatcher.howaboutit.data.database.ProtipEntity
import dreamcatcher.howaboutit.data.repositories.ItemsRepository
import dreamcatcher.howaboutit.data.repositories.ProtipsRepository

class FeedViewModel : ViewModel() {

    private val itemsRepository = ItemsRepository()
    private val protipsRepository = ProtipsRepository()

    fun updateItemsDatabaseWithServer(): LiveData<Boolean>? {
        return itemsRepository.updateDatabaseWithServer()
    }

    fun updateProtipsDatabaseWithServer(): LiveData<Boolean>? {
        return protipsRepository.updateDatabaseWithServer()
    }

    fun getAllItems(): LiveData<List<ItemEntity>>? {
        return itemsRepository.getAllItems()
    }

    fun getAllProtips(): LiveData<List<ProtipEntity>>? {
        return protipsRepository.getAllProtips()
    }

    fun getNetworkError(): LiveData<Boolean>? {
        return itemsRepository.getNetworkError()
    }
}