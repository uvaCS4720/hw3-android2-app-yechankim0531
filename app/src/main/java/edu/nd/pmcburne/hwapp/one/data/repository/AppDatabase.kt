package edu.nd.pmcburne.hwapp.one.data.repository
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [GameEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}