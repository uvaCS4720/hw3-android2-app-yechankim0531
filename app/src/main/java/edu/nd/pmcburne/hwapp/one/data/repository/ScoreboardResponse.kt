package edu.nd.pmcburne.hwapp.one.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ScoreboardResponse(
    val games: List<GameWrapper>?
)

@Serializable
data class GameWrapper(
    val game: GameDetail?
)

@Serializable
data class GameDetail(
    val gameID: String,
    val title: String? = null,
    val startTime: String? = null,
    val startDate: String? = null,
    val startTimeEpoch: String? = null,
    val gameState: String? = null,
    val currentPeriod: String? = null,
    val contestClock: String? = null,
    val finalMessage: String? = null,
    val network: String? = null,
    val url: String? = null,
    val home: TeamInfo? = null,
    val away: TeamInfo? = null
)

@Serializable
data class TeamInfo(
    val names: TeamNames? = null,
    val score: String? = null,
    val winner: Boolean? = null,
    val seed: String? = null,
    val rank: String? = null,
    val conferences: List<ConferenceInfo>? = null
)

@Serializable
data class TeamNames(
    val char6: String? = null,
    val short: String? = null,
    val seo: String? = null,
    val full: String? = null
)

@Serializable
data class ConferenceInfo(
    val conferenceName: String? = null,
    val conferenceSeo: String? = null
)