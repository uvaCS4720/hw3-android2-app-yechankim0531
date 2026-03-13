package edu.nd.pmcburne.hwapp.one.data.repository

import android.util.Log

class ScoresRepository(
    private val api: NcaaApiService,
    private val gameDao: GameDao
) {

    suspend fun getGamesForDate(gender: String, gameDate: String): List<GameEntity> {
        val games = gameDao.getGamesForDate(gender, gameDate)

        Log.d(
            "ScoresRepository",
            "DB READ -> gender=$gender date=$gameDate count=${games.size}"
        )

        val liveGame = games.firstOrNull { it.gameState == "live" }

        if (liveGame != null) {
            Log.d(
                "ScoresRepository",
                "LIVE DB GAME -> ${liveGame.awayTeamName} ${liveGame.awayScore} at ${liveGame.homeTeamName} ${liveGame.homeScore} clock=${liveGame.contestClock} period=${liveGame.currentPeriod}"
            )
        } else {
            Log.d("ScoresRepository", "LIVE DB GAME -> none")
        }

        return games
    }

    suspend fun refreshScores(
        gender: String,
        year: String,
        month: String,
        day: String
    ) {
        val gameDate = "$year-$month-$day"

        Log.d(
            "ScoresRepository",
            "REFRESH START -> gender=$gender date=$gameDate"
        )

        val response = api.getScoreboard(gender, year, month, day)

        Log.d(
            "ScoresRepository",
            "API RETURNED -> games=${response.games?.size ?: 0}"
        )

        val games = response.games?.mapNotNull { wrapper ->
            val game = wrapper.game ?: return@mapNotNull null

            GameEntity(
                gameId = game.gameID,
                gender = gender,
                gameDate = gameDate,

                homeTeamName = game.home?.names?.short
                    ?: game.home?.names?.char6
                    ?: "Unknown Home Team",

                awayTeamName = game.away?.names?.short
                    ?: game.away?.names?.char6
                    ?: "Unknown Away Team",

                homeScore = game.home?.score,
                awayScore = game.away?.score,

                gameState = game.gameState,
                startTime = game.startTime ?: game.startDate,
                currentPeriod = game.currentPeriod,
                contestClock = game.contestClock,
                finalMessage = game.finalMessage,

                homeWinner = game.home?.winner,
                awayWinner = game.away?.winner
            )
        } ?: emptyList()

        Log.d(
            "ScoresRepository",
            "MAPPED GAMES -> count=${games.size}"
        )

        val liveGame = games.firstOrNull { it.gameState == "live" }

        if (liveGame != null) {
            Log.d(
                "ScoresRepository",
                "LIVE API GAME -> ${liveGame.awayTeamName} ${liveGame.awayScore} at ${liveGame.homeTeamName} ${liveGame.homeScore} clock=${liveGame.contestClock} period=${liveGame.currentPeriod}"
            )
        } else {
            Log.d("ScoresRepository", "LIVE API GAME -> none")
        }

        gameDao.replaceGames(gender, gameDate, games)

        Log.d(
            "ScoresRepository",
            "DB REPLACE COMPLETE -> gender=$gender date=$gameDate"
        )
    }
}