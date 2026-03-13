package edu.nd.pmcburne.hwapp.one

import edu.nd.pmcburne.hwapp.one.data.repository.RetrofitInstance
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {

    @Test
    fun testApiReturnsGames() {
        runBlocking {
            val response = RetrofitInstance.api.getScoreboard(
                gender = "men",
                year = "2026",
                month = "03",
                day = "11"
            )

            assertNotNull("Response should not be null", response)
            assertNotNull("Games list should not be null", response.games)
            assertTrue("Should have at least one game", (response.games?.size ?: 0) > 0)

            println("Total games found: ${response.games?.size}")
            response.games?.take(3)?.forEach { wrapper ->
                val game = wrapper.game
                println("Game: ${game?.title}, State: ${game?.gameState}")
            }
        }
    }
}