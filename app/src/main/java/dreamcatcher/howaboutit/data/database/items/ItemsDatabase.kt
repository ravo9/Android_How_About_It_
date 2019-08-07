package dreamcatcher.howaboutit.data.database.items

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dreamcatcher.howaboutit.general.Converters

@Database(entities = [(ItemEntity::class)], version = 1)
@TypeConverters(Converters::class)
abstract class ItemsDatabase : RoomDatabase() {
    abstract fun getItemsDao(): ItemsDao
}