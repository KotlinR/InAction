package com.action.round.ui.screens.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.action.round.data.models.Training

class TimerViewModel(
    private val timer: Timer,
) : ViewModel() {

    val actualTimeLiveData: LiveData<String> = timer.actualTimeLiveData
    val trainingStatus: Boolean get() = timer.trainingStatus

    // TODO (возможно лишнее или поменять параметр на Training)
    private val _trainingLiveData = MutableLiveData<Training?>()
    val trainingLiveData: LiveData<Training?> = _trainingLiveData

    fun setTraining(training: Training?) {
        _trainingLiveData.value = training
        timer.setTrainingParameters(rounds = trainingLiveData.value?.exercises?.size)
    }

    fun startTraining() {
        timer.runTraining()
    }

    fun pauseTraining() {
        timer.pauseTraining()
    }

    fun loadTimerParameters() {
    }

    fun saveTimerParameters(
        totalRounds: Int?,
        countdown: Int,
        round: Int,
        relax: Int,
        preStart: Int,
        preStop: Int,
    ) {
        timer.saveTimeParameters(totalRounds, countdown, round, relax, preStart, preStop)
    }
}