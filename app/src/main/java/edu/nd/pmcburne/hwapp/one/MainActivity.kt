package edu.nd.pmcburne.hwapp.one

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import edu.nd.pmcburne.hwapp.one.data.repository.AppDatabase
import edu.nd.pmcburne.hwapp.one.data.repository.RetrofitInstance
import edu.nd.pmcburne.hwapp.one.data.repository.ScoresRepository
import edu.nd.pmcburne.hwapp.one.ui.ScoresScreen
import edu.nd.pmcburne.hwapp.one.ui.ScoresViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "scores_database"
        ).build()

        val repository = ScoresRepository(
            api = RetrofitInstance.api,
            gameDao = db.gameDao()
        )

        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ScoresViewModel(repository) as T
            }
        }

        val viewModel = ViewModelProvider(this, factory)[ScoresViewModel::class.java]

        setContent {
            ScoresScreen(viewModel = viewModel)
        }
    }
}