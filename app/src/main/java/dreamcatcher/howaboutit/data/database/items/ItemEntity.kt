package dreamcatcher.howaboutit.data.database.items

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
        @PrimaryKey val id: String,
        val name: String,
        val tags: List<String>,
        val imageLink: String,
        val binType: String,
        val additionalInformation: List<String>?)