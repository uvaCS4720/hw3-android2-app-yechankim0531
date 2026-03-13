package edu.nd.pmcburne.hwapp.one

import edu.nd.pmcburne.hwapp.one.data.repository.GameDao
import edu.nd.pmcburne.hwapp.one.data.repository.NcaaApiService
import edu.nd.pmcburne.hwapp.one.data.repository.ScoresRepository
import edu.nd.pmcburne.hwapp.one.data.model.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class RepositoryTest {

    @Test
    fun testRepositoryConvertsGameCorrectly() = runBlocking {

        val fakeApi = object : NcaaApiService {

            override suspend fun getScoreboard(
                gender: String,
                year: String,
                month: String,
                day: String
            ): ScoreboardResponse {

                val fakeGame = GameDetail(
                    gameID = "123",
                    title = "Test Game",
                    gameState = "final",
                    home = TeamInfo(
                        names = TeamNames(full = "Virginia"),
                        score = "70",
                        winner = true
                    ),
                    away = TeamInfo(
                        names = TeamNames(full = "Duke"),
                        score = "65",
                        winner = false
                    )
                )

                return ScoreboardResponse(
                    games = listOf(GameWrapper(fakeGame))
                )
            }
        }

        val fakeDao = object : GameDao {

            var storedGames = listOf<edu.nd.pmcburne.hwapp.one.data.repository.GameEntity>()

            override suspend fun insertGames(games: List<edu.nd.pmcburne.hwapp.one.data.repository.GameEntity>) {
                storedGames = games
            }

            override suspend fun getGamesForDate(
                gender: String,
                gameDate: String
            ): List<edu.nd.pmcburne.hwapp.one.data.repository.GameEntity> {
                return storedGames
            }

            override suspend fun deleteGamesForDate(gender: String, gameDate: String) {}
        }

        val repository = ScoresRepository(fakeApi, fakeDao)

        val games = repository.getScores("men", "2026", "03", "11")

        assertEquals(1, games.size)
        assertEquals("Virginia", games[0].homeTeamName)
        assertEquals("Duke", games[0].awayTeamName)
        assertEquals("70", games[0].homeScore)
        assertEquals("65", games[0].awayScore)
    }
}