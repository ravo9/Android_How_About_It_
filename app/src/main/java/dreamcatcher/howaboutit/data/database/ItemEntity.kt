package dreamcatcher.howaboutit.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity(tableName = "items")
data class ItemEntity(
        val name: String,
        val image: String?) {

        //@PrimaryKey(autoGenerate = true)
        //var id: Int? = null
}

