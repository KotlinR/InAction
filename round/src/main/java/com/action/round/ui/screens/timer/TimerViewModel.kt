package com.action.round.ui.screens.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.action.round.data.models.TimerParameters

class TimerViewModel(
    private val timer: Timer,
) : ViewModel() {

    val actualTimeLiveData: LiveData<String> = timer.actualTimeLiveData
    val actualRoundLiveData: LiveData<Pair<Int, String>> = timer.actualRoundLiveData
    val trainingStatus: Boolean get() = timer.trainingStatus
    val timerParameters: TimerParameters get() = timer.timerParameters
    private val originTimeParameters: TimerParameters = timer.originTimeParameters

    fun setTraining(
        totalRounds: Int?,
        onResultTotalRounds: (Int) -> Unit,
    ) {
        timer.setTrainingParameters(totalRounds = totalRounds) { resultTotalRounds ->
            onResultTotalRounds(resultTotalRounds)
        }
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

    fun next() {
        timer.next()
    }

    fun back(totalRounds: Int?) {
        timer.back(setRounds = totalRounds ?: timer.timerParameters.totalRounds)
    }

    fun resetToStart(totalRounds: Int?) {
        timer.resetToStart(totalRounds = totalRounds ?: timer.timerParameters.totalRounds)
    }

    fun resetTimerParameters(timerParameters: TimerParameters) {
        timer.resetTimeParameters(
            countdown = timerParameters.countdown,
            round = timerParameters.round,
            relax = timerParameters.relax,
            preStart = timerParameters.preStart,
            preStop = timerParameters.preStop,
            totalRounds = timerParameters.totalRounds,
        )
    }

    fun updateTimerParametersDB() {
        if (timerParameters != originTimeParameters) timer.updateTimerParameters()
    }
}