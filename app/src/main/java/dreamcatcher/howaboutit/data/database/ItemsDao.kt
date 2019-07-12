package dreamcatcher.howaboutit.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ItemsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewItem(itemEntity: ItemEntity)

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    fun getSingleSavedItemById(id: String): LiveData<ItemEntity>?

    @Query("SELECT * FROM items")
    fun getAllSavedItems(): LiveData<List<ItemEntity>>?

    @Query("DELETE FROM items")
    fun clearDatabase()
}