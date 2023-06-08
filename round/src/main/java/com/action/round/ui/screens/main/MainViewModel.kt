package com.action.round.ui.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.action.round.data.models.Training
import com.action.round.data.repos.TrainingRepository

class MainViewModel(
    private val trainingRepository: TrainingRepository,
) : ViewModel() {

    private val _trainingsLiveData = MutableLiveData<List<Training>>()
    val trainingsLiveData: LiveData<List<Training>> = _trainingsLiveData

    private val _openSecondScreenTrainingActivityLiveData = MutableLiveData<Training?>()
    val openSecondScreenTrainingActivityLiveData: LiveData<Training?> =
        _openSecondScreenTrainingActivityLiveData

    private val _openSecondScreenTimerActivityLiveData = MutableLiveData<Unit?>()
    val openSecondScreenTimerActivityLiveData: LiveData<Unit?> = _openSecondScreenTimerActivityLiveData


    init {
        getAllTrainings()
    }

    fun getAllTrainings() {
        trainingRepository.getAll { trainings ->
            _trainingsLiveData.postValue(trainings)
        }
    }

    fun openTrainingScreen(training: Training?) {
        _openSecondScreenTrainingActivityLiveData.value = training
    }

    fun openTimerScreen() {
        _openSecondScreenTimerActivityLiveData.value = null
    }

    fun deleteTraining(training: Training) {
        trainingRepository.delete(training) { updatedTrainings ->
            _trainingsLiveData.postValue(updatedTrainings)
        }
    }
}
