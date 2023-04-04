package com.action.round.ui.screens.training

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.action.round.data.Repository
import com.action.round.data.Training

class TrainingViewModel(
    private val repository: Repository,
    private val localExercisesRepository: LocalExercisesRepository,
) : ViewModel() {

    private val _exercisesLiveData = MutableLiveData<List<String>>()
    val exercisesLiveData: LiveData<List<String>> = _exercisesLiveData

    private val _trainingLiveData = MutableLiveData<Training?>()
    val trainingLiveData: LiveData<Training?> = _trainingLiveData

    fun displayTraining(training: Training?) {
        localExercisesRepository.setExercises(training?.exercises.orEmpty())
        _trainingLiveData.value = training
        _exercisesLiveData.value = localExercisesRepository.getExercises()
    }

    fun deleteExercise(position: Int) {
        localExercisesRepository.delete(position)
        _exercisesLiveData.value = localExercisesRepository.getExercises()
    }

    fun deleteTraining(training: Training?) {
        training?.let { repository.delete(it) {} }
    }

    fun saveTraining(training: Training) {
        if (training.id == null) {
            repository.save(training)
            _trainingLiveData.value = training
        } else {
            // TODO (обновление тренировки)
        }
    }

    fun clearExercises() {
//        _exercisesLiveData.value = listOf()
    }

    fun addNewExercise() {
        localExercisesRepository.add()
        _exercisesLiveData.value = localExercisesRepository.getExercises()
    }

    fun moveExercise(from: Int, to: Int) {
        localExercisesRepository.move(from, to)
        _exercisesLiveData.value = localExercisesRepository.getExercises()
    }

}