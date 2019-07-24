package dreamcatcher.howaboutit.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProtipsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewProtip(protipEntity: ProtipEntity)

    @Query("SELECT * FROM protips WHERE id = :id LIMIT 1")
    fun getSingleSavedProtipById(id: String): LiveData<ProtipEntity>?

    @Query("SELECT * FROM protips")
    fun getAllSavedProtips(): LiveData<List<ProtipEntity>>?

    @Query("DELETE FROM protips")
    fun clearDatabase()
}