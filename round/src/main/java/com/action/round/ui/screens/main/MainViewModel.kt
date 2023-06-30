package com.action.round.ui.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.action.round.analytics.Analytics
import com.action.round.data.models.Training
import com.action.round.data.repos.TrainingRepository
import java.util.concurrent.ExecutorService

class MainViewModel(
    private val analytics: Analytics,
    private val trainingRepository: TrainingRepository,
    private val es: ExecutorService,
) : ViewModel() {

    private val _trainingsLiveData = MutableLiveData<List<Training>>()
    val trainingsLiveData: LiveData<List<Training>> = _trainingsLiveData

    private val _openSecondScreenTrainingActivityLiveData = MutableLiveData<Training?>()
    val openSecondScreenTrainingActivityLiveData: LiveData<Training?> =
        _openSecondScreenTrainingActivityLiveData

    private val _openSecondScreenTimerActivityLiveData = MutableLiveData<Unit?>()
    val openSecondScreenTimerActivityLiveData: LiveData<Unit?> = _openSecondScreenTimerActivityLiveData

    private val originTrainings by lazy { mutableListOf<Training>() }
    private var sortBy = SortBy.NAME_ASC

    init {
        getAllTrainings()
    }

    fun getAllTrainings() {
        trainingRepository.getAll { trainings ->
            _trainingsLiveData.postValue(trainings.sorted())
        }
    }

    fun sortBy() {
        es.execute {
            trainingsLiveData.value?.let { trainings ->
                when (sortBy) {
                    SortBy.NAME_ASC -> {
                        sortBy = SortBy.NUMBER_OF_ROUNDS
                        _trainingsLiveData.postValue(trainings.sorted())
                    }
                    SortBy.NUMBER_OF_ROUNDS -> {
                        sortBy = SortBy.NAME_ASC
                        _trainingsLiveData.postValue(trainings.sorted())
                    }
                }
            }
        }
    }

    fun search(inputText: String) {
        es.execute {
            val searchResult = originTrainings
                .filter { it.title.contains(inputText, true) }
                .sortedBy { it.title }
            _trainingsLiveData.postValue(searchResult)
        }
    }

    fun preSearch() {
        es.execute {
            originTrainings.clear()
            originTrainings.addAll(trainingsLiveData.value.orEmpty())
        }
    }

    fun openTrainingScreen(training: Training?) {
        _openSecondScreenTrainingActivityLiveData.value = training
    }

    fun openTimerScreen() {
        analytics.logOpenTimer(Analytics.EVENT_PARAM_TIMER_SOURCE_MAIN)
        _openSecondScreenTimerActivityLiveData.value = null
    }

    fun deleteTraining(training: Training) {
        trainingRepository.delete(training) { updatedTrainings ->
            _trainingsLiveData.postValue(updatedTrainings.sorted())
        }
    }

    private fun List<Training>.sorted(): List<Training> {
        return when (sortBy) {
            SortBy.NAME_ASC -> sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })
            SortBy.NUMBER_OF_ROUNDS -> sortedByDescending { it.exercises.size }
        }
    }
}
