package com.action.round.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.action.round.data.Repository
import com.action.round.data.Training

class MainViewModel(
    private val repository: Repository,
) : ViewModel() {

    private val _trainingsLiveData = MutableLiveData<List<Training>>()
    val trainingsLiveData: LiveData<List<Training>> = _trainingsLiveData

    private val _openSecondScreenLiveData = MutableLiveData<Training?>()
    val openSecondScreenLiveData: LiveData<Training?> = _openSecondScreenLiveData

    init {
        repository.getAll { trainings ->
            _trainingsLiveData.postValue(trainings)
        }
    }

    fun openTrainingScreen(training: Training?) {
        _openSecondScreenLiveData.value = training
    }

    fun deleteTraining(training: Training) {
        repository.delete(training) { updatedTrainings ->
            _trainingsLiveData.postValue(updatedTrainings)
        }
    }
}