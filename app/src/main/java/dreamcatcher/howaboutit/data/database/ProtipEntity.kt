package dreamcatcher.howaboutit.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "protips")
data class ProtipEntity(
        @PrimaryKey val id: String,
        val protipText: String)