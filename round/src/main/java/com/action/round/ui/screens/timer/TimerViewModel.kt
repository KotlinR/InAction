package com.action.round.ui.screens.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class TimerViewModel(
    private val timer: Timer,
) : ViewModel() {

    val actualTimeLiveData: LiveData<String> = timer.actualTimeLiveData
    val actualRoundLiveData: LiveData<Pair<Int, String>> = timer.actualRoundLiveData
    val trainingStatus: Boolean get() = timer.trainingStatus

    fun setTraining(totalRounds: Int?) {
        timer.setTrainingParameters(
            totalRounds = totalRounds ?: timer.timerParameters.totalRounds
        )
    }

    fun startTraining() {
        timer.runTraining()
    }

    fun startFromThisRound(totalRounds: Int?, numberRound: Int) {
        timer.startFromThisRound(
            totalRounds = totalRounds ?: timer.timerParameters.totalRounds,
            numberRound = numberRound,
        )
    }

    fun pauseTraining() {
        timer.pauseTraining()
    }

    fun next(totalRounds: Int?) {
        timer.next()
    }

    fun back(totalRounds: Int?) {
        timer.back(
            setRounds = totalRounds ?: timer.timerParameters.totalRounds
        )
    }

    fun resetToStart(totalRounds: Int?) {
        timer.resetToStart(
            totalRounds = totalRounds ?: timer.timerParameters.totalRounds
        )
    }

    fun displayTimerParameters() {
    }

    fun resetTimerParameters(
        totalRounds: Int?,
        countdown: Int,
        round: Int,
        relax: Int,
        preStart: Int,
        preStop: Int,
    ) {
        timer.resetTimeParameters(totalRounds, countdown, round, relax, preStart, preStop)
    }

    fun updateTimerParametersDB() {
        timer.updateTimerParameters()
    }
}