package edu.nd.pmcburne.hwapp.one.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.nd.pmcburne.hwapp.one.data.repository.GameEntity
import edu.nd.pmcburne.hwapp.one.data.repository.ScoresRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ScoresUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedGender: String = "men",
    val games: List<GameEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ScoresViewModel(
    private val repository: ScoresRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScoresUiState())
    val uiState: StateFlow<ScoresUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var requestId = 0

    init {
        loadScores()
    }

    fun setGender(gender: String) {
        Log.d("ScoresViewModel", "SET GENDER -> $gender")
        _uiState.value = _uiState.value.copy(selectedGender = gender)
        loadScores()
    }

    fun setDate(date: LocalDate) {
        Log.d("ScoresViewModel", "SET DATE -> $date")
        _uiState.value = _uiState.value.copy(selectedDate = date)
        loadScores()
    }

    fun refresh() {
        Log.d("ScoresViewModel", "REFRESH PRESSED")
        loadScores()
    }

    private fun loadScores() {
        val date = _uiState.value.selectedDate
        val gender = _uiState.value.selectedGender
        val gameDate =
            "${date.year}-${date.monthValue.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}"

        requestId += 1
        val currentRequestId = requestId

        Log.d(
            "ScoresViewModel",
            "LOAD START -> requestId=$currentRequestId gender=$gender gameDate=$gameDate"
        )

        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                repository.refreshScores(
                    gender = gender,
                    year = date.year.toString(),
                    month = date.monthValue.toString().padStart(2, '0'),
                    day = date.dayOfMonth.toString().padStart(2, '0')
                )

                val games = repository.getGamesForDate(
                    gender = gender,
                    gameDate = gameDate
                )

                Log.d(
                    "ScoresViewModel",
                    "LOAD SUCCESS -> requestId=$currentRequestId games=${games.size}"
                )

                _uiState.value = _uiState.value.copy(
                    games = games,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                Log.d(
                    "ScoresViewModel",
                    "LOAD ERROR -> requestId=$currentRequestId message=${e.message}"
                )

                val cachedGames = repository.getGamesForDate(
                    gender = gender,
                    gameDate = gameDate
                )

                _uiState.value = _uiState.value.copy(
                    games = cachedGames,
                    isLoading = false,
                    errorMessage = if (cachedGames.isEmpty()) {
                        "Failed to load: ${e.message}"
                    } else {
                        "Showing saved data"
                    }
                )
            }
        }
    }
}