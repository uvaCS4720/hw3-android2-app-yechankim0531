package edu.nd.pmcburne.hwapp.one.data.repository

import edu.nd.pmcburne.hwapp.one.data.model.ScoreboardResponse
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface NcaaApiService {
    @GET("scoreboard/basketball-{gender}/d1/{year}/{month}/{day}")
    suspend fun getScoreboard(
        @Path("gender") gender: String,
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("day") day: String
    ): ScoreboardResponse
}

object RetrofitInstance {
    private val json = Json { ignoreUnknownKeys = true }

    val api: NcaaApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://ncaa-api.henrygd.me/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(NcaaApiService::class.java)
    }
}