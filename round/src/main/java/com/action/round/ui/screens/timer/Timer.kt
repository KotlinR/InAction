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
    val actualTimeLiveData: LiveData<String> get() = _actualTimeLiveData

    private val _actualRoundLiveData = MutableLiveData<Pair<Int, String>>()
    val actualRoundLiveData: LiveData<Pair<Int, String>> get() = _actualRoundLiveData

    private var _trainingStatus: Boolean = false
    val trainingStatus: Boolean get() = _trainingStatus

    private var _totalRounds = 0
    private val totalRounds: Int get() = _totalRounds

    private var _totalRelax = 0
    private val totalRelax: Int get() = _totalRelax

    private var _currentRounds = 1
    private val currentRounds: Int get() = _currentRounds

    private var _havingCountdown: Boolean = false
    private val havingCountdown: Boolean get() = _havingCountdown

    private var _currentTime: Int = 0
    private val currentTime: Int get() = _currentTime

    private val _timerParameters: TimerParameters = TimerParameters()
    val timerParameters get() = _timerParameters

    fun pauseTraining() {
        es.execute {
            _trainingStatus = false
        }
    }

    fun next() {
        es.execute {
            if (havingCountdown) {
                !havingCountdown
                _currentTime = timerParameters.round
                _actualRoundLiveData.postValue(Pair(currentRounds, "ROUND $currentRounds"))
            }
            if (!havingCountdown && totalRelax != 0 && totalRounds >= 1) {
                when (totalRounds) {
                    totalRelax + 1 -> {
                        _totalRounds--
                        _currentRounds++
                        _currentTime = timerParameters.relax
                        _actualRoundLiveData.postValue(Pair(currentRounds, "NEXT ROUND $currentRounds"))
                    }
                    totalRelax -> {
                        _totalRelax--
                        _currentTime = timerParameters.round
                        _actualRoundLiveData.postValue(Pair(currentRounds, "ROUND $currentRounds"))
                    }
                }
            }
            setTimeToDisplay(currentTime)
        }
    }

    fun back(setRounds: Int) {
        es.execute {
            if (totalRounds == setRounds) {
                when {
                    timerParameters.countdown != 0 && currentTime == timerParameters.round -> {
                        _havingCountdown = true
                        _currentTime = timerParameters.countdown
                        _actualRoundLiveData.postValue(Pair(currentRounds, "WILL START IN:"))
                    }
                    else -> {
                        _currentTime = timerParameters.round
                        _actualRoundLiveData.postValue(Pair(currentRounds, "ROUND $currentRounds"))
                    }
                }
            }
            if (totalRounds < setRounds) {
                when (totalRounds) {
                    totalRelax + 1 -> {
                        if (currentTime != timerParameters.round) {
                            _currentTime = timerParameters.round
                            _actualRoundLiveData.postValue(Pair(currentRounds, "ROUND $currentRounds"))
                        } else {
                            _totalRelax++
                            _currentTime = timerParameters.relax
                            _actualRoundLiveData.postValue(Pair(currentRounds, "NEXT ROUND $currentRounds"))
                        }
                    }
                    totalRelax -> {
                        if (currentTime != timerParameters.relax) {
                            _currentTime = timerParameters.relax
                            _actualRoundLiveData.postValue(Pair(currentRounds, "NEXT ROUND $currentRounds"))
                        } else {
                            _totalRounds++
                            _currentRounds--
                            _currentTime = timerParameters.round
                            _actualRoundLiveData.postValue(Pair(currentRounds, "ROUND $currentRounds"))
                        }
                    }
                }
            }
            setTimeToDisplay(currentTime)
        }
    }

    fun resetToStart(totalRounds: Int) {
        es.execute {
            preparationsForStart(totalRounds)
            setTimeToDisplay(currentTime)
            _currentRounds = 1
            if (havingCountdown) {
                _actualRoundLiveData.postValue(Pair(currentRounds, "WILL START IN:"))
            } else {
                _actualRoundLiveData.postValue(Pair(currentRounds, "ROUND $currentRounds"))
            }
        }
    }

    fun updateTimerParameters() = timerParametersRepository.updateTimerParameters(timerParameters)

    fun setTrainingParameters(totalRounds: Int) {
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
            preparationsForStart(totalRounds)
            setTimeToDisplay(currentTime)
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
                _actualRoundLiveData.postValue(Pair(currentRounds, "WILL START IN:"))
                val timeCountdown = if (currentTime == 0) timerParameters.countdown else _currentTime
                timer(timeCountdown, timerParameters.preStart)
                if (currentTime == 0) {
                    _havingCountdown = false
                }
            }

            while (trainingStatus && totalRounds > 0) {
                if (totalRounds > totalRelax) {
                    _actualRoundLiveData.postValue(Pair(currentRounds, "ROUND $currentRounds"))
                    val timeRound = if (currentTime == 0) timerParameters.round else _currentTime
                    timer(timeRound, timerParameters.preStop)
                    if (currentTime == 0) {
                        _totalRounds--
                        _currentRounds++
                    }
                }
                if (totalRounds == totalRelax) {
                    _actualRoundLiveData.postValue(Pair(currentRounds, "NEXT ROUND $currentRounds"))
                    val timeRelax = if (currentTime == 0) timerParameters.relax else _currentTime
                    timer(timeRelax, timerParameters.preStart)
                    if (currentTime == 0) {
                        _totalRelax--
                    }
                }
            }
        }
    }

    fun startFromThisRound(totalRounds: Int, numberRound: Int) {
        preparationsForStart(totalRounds - numberRound + 1)
        setTimeToDisplay(currentTime)
        _currentRounds = numberRound
        _actualRoundLiveData.postValue(Pair(currentRounds, "ROUND $currentRounds"))
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
                // TODO (звуковой сигнал + маргание таймера(смена цвета))
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

    private fun preparationsForStart(totalRounds: Int) {
        _havingCountdown = timerParameters.countdown != 0
        _totalRounds = totalRounds
        _totalRelax = totalRounds - 1
        _currentTime = if (havingCountdown) timerParameters.countdown else timerParameters.round
    }

    private fun setTimeToDisplay(currentTime: Int) {
        val counterMin: Int = currentTime / 60
        val counterSec: Int = currentTime - (counterMin * 60)

        val min = if (counterMin < 10) "0$counterMin" else "$counterMin"
        val sec = if (counterSec < 10) "0$counterSec" else "$counterSec"

        _actualTimeLiveData.postValue("$min:$sec")
    }
}