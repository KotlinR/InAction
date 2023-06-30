package com.action.round.ui.screens.training

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.action.round.analytics.Analytics
import com.action.round.data.models.Exercise
import com.action.round.data.models.Training
import com.action.round.data.repos.LocalExercisesRepository
import com.action.round.data.repos.TrainingRepository

class TrainingViewModel(
    private val analytics: Analytics,
    private val trainingRepository: TrainingRepository,
    private val localExercisesRepository: LocalExercisesRepository,
) : ViewModel() {

    val exercisesLiveData: LiveData<List<Exercise>> = localExercisesRepository.exercisesLiveData
    val exercisesSize: Int get() = localExercisesRepository.exercises.size

    private val _openSecondScreenLiveData = MutableLiveData<List<Exercise>>()
    val openSecondScreenLiveData: LiveData<List<Exercise>> = _openSecondScreenLiveData

    fun setTraining(training: Training?) {
        localExercisesRepository.setExercises(training?.exercises.orEmpty())
    }

    fun saveTraining(
        title: String,
    ) {
        trainingRepository.save(
            title = title,
            exercises = filledExercises(),
        )
    }

    fun updateTraining(
        id: Int,
        title: String,
    ) {
        trainingRepository.update(
            id = id,
            title = title,
            exercises = filledExercises(),
        )
    }

    fun addNewExercise() {
        localExercisesRepository.add()
    }

    fun moveExercise(from: Int, to: Int) {
        localExercisesRepository.move(from, to)
    }

    fun deleteExercise(position: Int) {
        localExercisesRepository.deleteByPosition(position)
    }

    fun updateExerciseById(id: Int, newDescription: String) {
        localExercisesRepository.updateExerciseById(id, newDescription)
    }

    private fun filledExercises(): List<String> {
        return localExercisesRepository.exercises.mapNotNull { ex ->
            ex.takeIf { it.description.isNotEmpty() }?.description
        }
    }

    fun openTimerScreen(currentExercise: List<Exercise>) {
        analytics.logOpenTimer(Analytics.EVENT_PARAM_TIMER_SOURCE_TRAINING)
        _openSecondScreenLiveData.value = currentExercise
    }
}
