package edu.nd.pmcburne.hwapp.one.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import edu.nd.pmcburne.hwapp.one.data.repository.GameEntity
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoresScreen(viewModel: ScoresViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            ScoresTopBar(
                date = uiState.selectedDate,
                gender = uiState.selectedGender,
                onDateSelected = { viewModel.setDate(it) },
                onGenderSelected = { viewModel.setGender(it) }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { viewModel.refresh() },
                    enabled = !uiState.isLoading
                ) {
                    Text(if (uiState.isLoading) "Loading" else "Refresh")
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            if (!uiState.isLoading && uiState.games.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No games found")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.games) { game ->
                        GameCard(game)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoresTopBar(
    date: LocalDate,
    gender: String,
    onDateSelected: (LocalDate) -> Unit,
    onGenderSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var genderExpanded by remember { mutableStateOf(false) }

    val dateText = "${date.monthValue}/${date.dayOfMonth}/${date.year}"
    val genderText = if (gender == "men") "Men" else "Women"

    TopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.clickable {
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
                            },
                            date.year,
                            date.monthValue - 1,
                            date.dayOfMonth
                        ).show()
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("▼")
                }

                Box {
                    Row(
                        modifier = Modifier.clickable {
                            genderExpanded = true
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = genderText,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("▼")
                    }

                    DropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Men") },
                            onClick = {
                                onGenderSelected("men")
                                genderExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Women") },
                            onClick = {
                                onGenderSelected("women")
                                genderExpanded = false
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun GameCard(game: GameEntity) {
    val awayIsWinner = game.awayWinner == true
    val homeIsWinner = game.homeWinner == true

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Left side: stacked team names
                Column(
                    modifier = Modifier.weight(1.8f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeamLine(
                        teamName = game.awayTeamName,
                        isWinner = awayIsWinner
                    )
                    TeamLine(
                        teamName = game.homeTeamName,
                        isWinner = homeIsWinner
                    )
                }

                // Middle: stacked scores
                Column(
                    modifier = Modifier.weight(0.7f),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ScoreLine(
                        score = game.awayScore,
                        isWinner = awayIsWinner
                    )
                    ScoreLine(
                        score = game.homeScore,
                        isWinner = homeIsWinner
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Right side: game status
                Column(
                    modifier = Modifier.weight(1.2f),
                    horizontalAlignment = Alignment.Start
                ) {
                    StatusBlock(game)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${game.awayTeamName} at ${game.homeTeamName}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
@Composable
fun TeamLine(
    teamName: String,
    isWinner: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = teamName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun ScoreLine(
    score: String?,
    isWinner: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {


        Text(
            text = score?.takeIf { it.isNotBlank() } ?: "-",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal
        )
        if (isWinner) {
            Text(
                text = "◀",
                modifier = Modifier.padding(end = 4.dp)
            )
        }
        if(!isWinner){
            Text(
                text = " ",
                modifier = Modifier.padding(end = 4.dp)
            )
        }
    }
}

@Composable
fun StatusBlock(game: GameEntity) {
    when (game.gameState) {
        "final" -> {
            Text(
                text = "Final",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        "live" -> {
            val period = game.currentPeriod?.takeIf { it.isNotBlank() } ?: "-"
            val clock = game.contestClock?.takeIf { it.isNotBlank() } ?: "-"

            Text(
                text = "$clock - $period",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        "pre" -> {
            Text(
                text = game.startTime?.takeIf { it.isNotBlank() } ?: "TBD",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        else -> {
            Text(
                text = game.gameState ?: "Unknown",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}