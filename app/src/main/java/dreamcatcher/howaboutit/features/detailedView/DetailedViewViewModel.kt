package dreamcatcher.howaboutit.features.detailedView

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dreamcatcher.howaboutit.data.database.ItemEntity
import dreamcatcher.howaboutit.data.repositories.ItemsRepository

class DetailedViewViewModel : ViewModel() {

    private var itemsRepository = ItemsRepository()

    fun getSingleSavedItemById(itemId: String): LiveData<ItemEntity>? {
        return itemsRepository.getSingleSavedItemById(itemId)
    }
}