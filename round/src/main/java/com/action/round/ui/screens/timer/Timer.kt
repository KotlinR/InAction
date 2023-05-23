package com.action.round.ui.screens.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.action.round.data.models.TimerParameters
import com.action.round.data.repos.TimerParametersRepository
import java.util.concurrent.ExecutorService

class Timer(
    private val es: ExecutorService,
    private val timerParametersRepository: TimerParametersRepository,
) {

    private val _actualTimeLiveData = MutableLiveData<String>()
    val actualTimeLiveData: LiveData<String> = _actualTimeLiveData

    private var _trainingStatus: Boolean = false
    val trainingStatus: Boolean get() = _trainingStatus

    private var _totalRounds: Int = 0
    private val totalRounds: Int get() = _totalRounds

    private var _totalRelax: Int = 0
    private val totalRelax get() = _totalRelax

    private var _havingCountdown: Boolean = false
    private val havingCountdown: Boolean get() = _havingCountdown

    private var _currentTime: Int = 0
    private val currentTime: Int get() = _currentTime

    private val _timerParameters: TimerParameters = TimerParameters()
    private val timerParameters get() = _timerParameters

    fun pauseTraining() {
        es.execute {
            _trainingStatus = false
        }
    }

    fun setTrainingParameters(rounds: Int?) {
        es.execute {
            setTimerParameters()
            setTotalRoundsAndRelax(rounds)
        }
    }

    fun resetTimeParameters(
        totalRounds: Int?,
        countdown: Int,
        round: Int,
        relax: Int,
        preStart: Int,
        preStop: Int,
    ) {
        es.execute {
            if (countdown != timerParameters.countdown) _timerParameters.countdown = countdown
            if (round != timerParameters.countdown) _timerParameters.round = round
            if (relax != timerParameters.relax) _timerParameters.relax = relax
            if (preStart != timerParameters.preStart) _timerParameters.preStart = preStart
            if (preStop != timerParameters.preStop) _timerParameters.preStop = preStop
            if (totalRounds != null) _timerParameters.round = totalRounds
        }
    }

    fun runTraining() {
        es.execute {
            _trainingStatus = true

            if (timerParameters.countdown != 0 && havingCountdown) {
                val timeCountdown =
                    if (currentTime == 0) timerParameters.countdown else _currentTime
                timer(timeCountdown, timerParameters.preStart)
                if (currentTime == 0) {
                    _havingCountdown = false
                }
            }

            while (trainingStatus && totalRounds > 0) {
                if (totalRounds > totalRelax) {
                    val timeRound = if (currentTime == 0) timerParameters.round else _currentTime
                    timer(timeRound, timerParameters.preStop)
                    if (currentTime == 0) {
                        _totalRounds = totalRounds - 1
                    }
                }
                if (totalRounds == totalRelax) {
                    val timeRelax = if (currentTime == 0) timerParameters.relax else _currentTime
                    timer(timeRelax, timerParameters.preStart)
                    if (currentTime == 0) {
                        _totalRelax = totalRelax - 1
                    }
                }
            }
        }
    }

    private fun timer(setTime: Int, timePreFinish: Int) {
        _currentTime = setTime

        var sec: String
        var min: String
        var counterMin: Int = currentTime / 60
        var counterSec: Int = currentTime - (counterMin * 60)

        while (currentTime > 0 && trainingStatus) {

            min = if (counterMin < 10) "0$counterMin" else "$counterMin"
            sec = if (counterSec < 10) "0$counterSec" else "$counterSec"

            _actualTimeLiveData.postValue("$min:$sec")

            if (timePreFinish > 0 && counterMin == 0 && counterSec == timePreFinish) {
                // TODO (звуковой сигнал)
            }

            if (counterSec == 0) {
                counterMin--
                counterSec = 60
            }

            _currentTime--
            counterSec--

            Thread.sleep(1000)
        }
    }

    private fun stopwatch(time: Int) {
        es.execute {
            var counterSec = 0
            var counterMin = 0
            var sec = "00"
            var min = "00"
            (0..time).forEach {
                Thread.sleep(1000)

                sec = if (counterSec < 10) "0$counterSec" else "$counterSec"
                min = if (counterMin < 10) "0$counterMin" else "$counterMin"

                if (counterSec == 60) {
                    counterSec = 0
                    counterMin++
                    sec = "00"
                }
                counterSec++

                _actualTimeLiveData.postValue("${min}:${sec}")
            }
        }
    }

    fun updateTimerParameters() {
        timerParametersRepository.updateTimerParameters(timerParameters)
    }

    private fun setTimerParameters() {
        timerParametersRepository.getTimerParameters { parameters ->
            parameters?.let {
                _timerParameters.apply {
                    countdown = parameters.countdown
                    round = parameters.round
                    relax = parameters.relax
                    preStart = parameters.preStart
                    preStop = parameters.preStop
                    round = parameters.totalRounds
                }
            }
        }
        _havingCountdown = timerParameters.countdown != 0
    }

    private fun setTotalRoundsAndRelax(rounds: Int?) {
        if (rounds != null) {
            _totalRounds = rounds
            _totalRelax = rounds - 1
        } else {
            _totalRounds = _timerParameters.totalRounds
            _totalRelax = totalRounds - 1
        }
    }
}