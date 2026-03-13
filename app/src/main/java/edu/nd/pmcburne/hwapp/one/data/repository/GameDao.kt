package edu.nd.pmcburne.hwapp.one.data.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface GameDao {

    @Query("SELECT * FROM games WHERE gender = :gender AND gameDate = :gameDate")
    suspend fun getGamesForDate(gender: String, gameDate: String): List<GameEntity>

    @Transaction
    suspend fun replaceGames(gender: String, gameDate: String, games: List<GameEntity>) {
        deleteGamesForDate(gender, gameDate)
        insertGames(games)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameEntity>)

    @Query("DELETE FROM games WHERE gender = :gender AND gameDate = :gameDate")
    suspend fun deleteGamesForDate(gender: String, gameDate: String)
}
