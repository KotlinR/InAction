package com.action.round

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.Room
import com.action.round.data.converters.TrainingConverter
import com.action.round.data.db.InActionDatabase
import com.action.round.data.db.TrainingDao
import com.action.round.data.repos.LocalExercisesRepository
import com.action.round.data.repos.Repository
import com.action.round.ui.screens.main.MainViewModel
import com.action.round.ui.screens.training.TrainingViewModel
import java.util.concurrent.Executors

class Dependencies(context: Context) {
    companion object {
        val Context.dependencies: Dependencies
            get() = when (this) {
                is App -> dependencies
                else -> applicationContext.dependencies
            }

        private const val DATABASE_NAME = "database.db"
    }

    val mainViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactory {
            initializer {
                MainViewModel(repository)
            }
        }

    val trainingViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactory {
            initializer {
                TrainingViewModel(
                    repository = repository,
                    localExercisesRepository = localExercisesRepository,
                )
            }
        }

    private val db: InActionDatabase = Room.databaseBuilder(
        context,
        InActionDatabase::class.java,
        DATABASE_NAME,
    ).build()

    private val repository: Repository
        get() = Repository(
            trainingDao = dao,
            es = Executors.newSingleThreadExecutor(),
            trainingConverter = trainingConverter,
        )

    private val trainingConverter: TrainingConverter
        get() = TrainingConverter()

    private val dao: TrainingDao
        get() = db.trainingDao()

    private val localExercisesRepository: LocalExercisesRepository
        get() = LocalExercisesRepository(
            es = Executors.newSingleThreadExecutor(),
        )
}
