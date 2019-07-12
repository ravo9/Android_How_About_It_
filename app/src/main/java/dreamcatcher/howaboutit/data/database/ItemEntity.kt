package dreamcatcher.howaboutit.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
        @PrimaryKey val id: String,
        val name: String,
        val tags: List<String>,
        val recyclingSteps: List<String>,
        val imageLink: String)