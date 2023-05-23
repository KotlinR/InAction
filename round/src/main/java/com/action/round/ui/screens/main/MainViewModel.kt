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

    private val _openSecondScreenLiveData = MutableLiveData<Training?>()
    val openSecondScreenLiveData: LiveData<Training?> = _openSecondScreenLiveData

    init {
        getAllTrainings()
    }

    fun getAllTrainings() {
        trainingRepository.getAll { trainings ->
            _trainingsLiveData.postValue(trainings)
        }
    }

    fun openTrainingScreen(training: Training?) {
        _openSecondScreenLiveData.value = training
    }

    fun deleteTraining(training: Training) {
        trainingRepository.delete(training) { updatedTrainings ->
            _trainingsLiveData.postValue(updatedTrainings)
        }
    }
}
