package edu.nd.pmcburne.hwapp.one.data.repository

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey
    val gameId: String,

    val gender: String,
    val gameDate: String,

    val homeTeamName: String,
    val awayTeamName: String,

    val homeScore: String?,
    val awayScore: String?,

    val gameState: String?,
    val startTime: String?,
    val currentPeriod: String?,
    val contestClock: String?,
    val finalMessage: String?,

    val homeWinner: Boolean?,
    val awayWinner: Boolean?
)