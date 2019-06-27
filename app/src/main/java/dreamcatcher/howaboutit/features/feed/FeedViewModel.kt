package dreamcatcher.howaboutit.features.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dreamcatcher.howaboutit.data.database.ItemEntity
import dreamcatcher.howaboutit.data.repositories.ItemsRepository

class FeedViewModel : ViewModel() {

    private lateinit var itemsRepository: ItemsRepository

    /*fun getAllItems(): LiveData<List<ItemEntity>>? {
        return itemsRepository.getAllItems()
    }*/

    /*fun getNetworkError(): LiveData<Boolean>? {
        return itemsRepository.getNetworkError()
    }*/
}