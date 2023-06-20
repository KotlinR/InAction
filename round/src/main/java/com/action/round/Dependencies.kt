package com.action.round

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.Room
import com.action.round.data.converters.TimerParametersConverter
import com.action.round.data.converters.TrainingConverter
import com.action.round.data.db.InActionDatabase
import com.action.round.data.db.TimerParametersDao
import com.action.round.data.db.TrainingDao
import com.action.round.data.repos.LocalExercisesRepository
import com.action.round.data.repos.TimerParametersRepository
import com.action.round.data.repos.TrainingRepository
import com.action.round.ui.screens.main.MainViewModel
import com.action.round.ui.screens.timer.Timer
import com.action.round.ui.screens.timer.TimerViewModel
import com.action.round.ui.screens.training.TrainingViewModel
import java.util.concurrent.Executors

class Dependencies(private val context: Context) {
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
                MainViewModel(
                    trainingRepository = trainingRepository,
                    es = Executors.newSingleThreadExecutor(),
                )
            }
        }

    val trainingViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactory {
            initializer {
                TrainingViewModel(
                    trainingRepository = trainingRepository,
                    localExercisesRepository = localExercisesRepository,
                )
            }
        }

    val timerViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactory {
            initializer {
                TimerViewModel(
                    timer = timer,
                )
            }
        }

    private val db: InActionDatabase = Room.databaseBuilder(
        context = context,
        klass = InActionDatabase::class.java,
        name = DATABASE_NAME,
    ).build()

    private val trainingRepository: TrainingRepository
        get() = TrainingRepository(
            trainingDao = trainingDao,
            es = Executors.newSingleThreadExecutor(),
            trainingConverter = trainingConverter,
        )

    private val trainingConverter: TrainingConverter
        get() = TrainingConverter()

    private val trainingDao: TrainingDao
        get() = db.trainingDao()

    private val localExercisesRepository: LocalExercisesRepository
        get() = LocalExercisesRepository(
            es = Executors.newSingleThreadExecutor(),
        )

    private val timer: Timer
        get() = Timer(
            es = Executors.newCachedThreadPool(),
            timerParametersRepository = timerParametersRepository,
            context = context,
        )

    private val timerParametersRepository: TimerParametersRepository
        get() = TimerParametersRepository(
            timerParametersDao = timerParametersDao,
            es = Executors.newSingleThreadExecutor(),
            timerParametersConverter = timerParametersConverter
        )

    private val timerParametersConverter: TimerParametersConverter
        get() = TimerParametersConverter()

    private val timerParametersDao: TimerParametersDao
        get() = db.timerParametersDao()
}
