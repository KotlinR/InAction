package com.action.round.ui.screens.timer

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.action.round.R
import com.action.round.data.models.TimerParameters
import com.action.round.data.repos.TimerParametersRepository
import java.util.concurrent.ExecutorService

class Timer(
    private val es: ExecutorService,
    private val timerParametersRepository: TimerParametersRepository,
    private val context: Context,
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


    private val _originTimeParameters by lazy { TimerParameters() }
    val originTimeParameters get() = _originTimeParameters
    private val _timerParameters by lazy { TimerParameters() }
    val timerParameters get() = _timerParameters

    private var mediaPlayer: MediaPlayer? = null

    fun pauseTraining() {
        es.execute {
            _trainingStatus = false
        }
        mediaPlayer?.release() // todo: find a better place for resource releasing
    }

    fun next() {
        es.execute {
            if (havingCountdown) {
                _havingCountdown = false
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
            setTimeToDisplay(currentTime = currentTime)
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
                    !havingCountdown -> {
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
            preparationsForStart(totalRounds = totalRounds)
            setTimeToDisplay(currentTime = currentTime)
            _currentRounds = 1
            if (havingCountdown) {
                _actualRoundLiveData.postValue(Pair(currentRounds, "WILL START IN:"))
            } else {
                _actualRoundLiveData.postValue(Pair(currentRounds, "ROUND $currentRounds"))
            }
        }
    }

    fun updateTimerParameters() {
        timerParametersRepository.updateTimerParameters(timerParameters = timerParameters)
    }


    fun setTrainingParameters(
        totalRounds: Int?,
        onResultTotalRounds: (Int) -> Unit,
    ) {
        timerParametersRepository.getTimerParameters { parameters ->
            parameters?.let {
                Log.d("!!!", parameters.toString())
                _timerParameters.apply {
                    this.countdown = it.countdown
                    this.round = it.round
                    this.relax = it.relax
                    this.preStart = it.preStart
                    this.preStop = it.preStop
                    this.totalRounds = it.totalRounds
                }
                onResultTotalRounds(totalRounds ?: timerParameters.totalRounds)
                _originTimeParameters.apply {
                    this.countdown = it.countdown
                    this.round = it.round
                    this.relax = it.relax
                    this.preStart = it.preStart
                    this.preStop = it.preStop
                    this.totalRounds = it.totalRounds
                }
            }
            preparationsForStart(totalRounds = totalRounds ?: timerParameters.totalRounds)
            setTimeToDisplay(currentTime = currentTime)
        }
    }

    fun resetTimeParameters(
        newTP: TimerParameters,
        totalRounds: Int?,
    ) {
        es.execute {
            if (newTP.countdown != timerParameters.countdown) _timerParameters.countdown = newTP.countdown
            if (newTP.round != timerParameters.round) _timerParameters.round = newTP.round
            if (newTP.relax != timerParameters.relax) _timerParameters.relax = newTP.relax
            if (newTP.preStart != timerParameters.preStart) _timerParameters.preStart = newTP.preStart
            if (newTP.preStop != timerParameters.preStop) _timerParameters.preStop = newTP.preStop
            if (newTP.totalRounds != timerParameters.totalRounds) _timerParameters.totalRounds =
                newTP.totalRounds

            if (!trainingStatus) {
                resetToStart(totalRounds ?: timerParameters.totalRounds)
            }
        }
    }

    fun runTraining() {
        es.execute {
            _trainingStatus = true

            if (timerParameters.countdown != 0 && havingCountdown) {
                _actualRoundLiveData.postValue(Pair(currentRounds, "WILL START IN:"))
                val timeCountdown = if (currentTime == 0) timerParameters.countdown else _currentTime
                timer(setTime = timeCountdown, isRound = false)
                if (currentTime == 0) {
                    _havingCountdown = false
                }
            }
            while (trainingStatus && totalRounds > 0) {
                if (totalRounds > totalRelax) {
                    Log.d("!!!", "Round:$totalRounds Relax:$totalRelax")
                    _actualRoundLiveData.postValue(Pair(currentRounds, "ROUND $currentRounds"))
                    val timeRound = if (currentTime == 0) timerParameters.round else _currentTime
                    timer(setTime = timeRound, isRound = true)
                    if (currentTime == 0) {
                        _totalRounds--
                        _currentRounds++
                    }
                    Log.d("!!!", "Round:$totalRounds Relax:$totalRelax")
                }
                if (totalRounds == totalRelax && totalRelax != 0) {
                    _actualRoundLiveData.postValue(Pair(currentRounds, "NEXT ROUND $currentRounds"))
                    val timeRelax = if (currentTime == 0) timerParameters.relax else _currentTime
                    timer(setTime = timeRelax, isRound = false)
                    if (currentTime == 0) {
                        _totalRelax--
                    }
                }
            }
            if (trainingStatus && totalRounds == 0 && totalRelax == 0) {
                pauseTraining()
                _actualRoundLiveData.postValue(Pair(0, "TRAINING COMPLETED"))
                _actualTimeLiveData.postValue("00:00")
            }
        }
    }

    fun startFromThisRound(totalRounds: Int, numberRound: Int) {
        _currentRounds = numberRound
        preparationsForStart(totalRounds - numberRound + 1)
        setTimeToDisplay(currentTime)
    }

    private fun timer(setTime: Int, isRound: Boolean) {
        _currentTime = setTime

        var sec: String
        var min: String
        var counterMin: Int = currentTime / 60
        var counterSec: Int = currentTime - (counterMin * 60)

        while (currentTime > 0 && trainingStatus) {

            min = if (counterMin < 10) "0$counterMin" else "$counterMin"
            sec = if (counterSec < 10) "0$counterSec" else "$counterSec"

            _actualTimeLiveData.postValue("$min:$sec")

            if (currentTime == timerParameters.round && isRound) {
                mediaPlayer = MediaPlayer.create(context, R.raw.gong_start).apply { start() }
            }

            if (currentTime == 1 && isRound) {
                mediaPlayer = MediaPlayer.create(context, R.raw.gong_stop).apply { start() }
            }

            if (timerParameters.preStop > 0 && counterMin == 0 && counterSec == timerParameters.preStop) {
                mediaPlayer = MediaPlayer.create(context, R.raw.pre).apply { start() }
            }

            if (timerParameters.preStart > 0 && counterMin == 0 && counterSec == timerParameters.preStart) {
                mediaPlayer = MediaPlayer.create(context, R.raw.pre).apply { start() }
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

    private fun preparationsForStart(totalRounds: Int) {
        _havingCountdown = timerParameters.countdown != 0
        _totalRounds = totalRounds
        _totalRelax = totalRounds - 1
        _currentTime = if (havingCountdown) timerParameters.countdown else timerParameters.round

        if (havingCountdown) {
            _actualRoundLiveData.postValue(Pair(currentRounds, "WILL START IN:"))
        } else {
            _actualRoundLiveData.postValue(Pair(currentRounds, "ROUND $currentRounds"))
        }
    }

    private fun setTimeToDisplay(currentTime: Int) {
        val counterMin: Int = currentTime / 60
        val counterSec: Int = currentTime - (counterMin * 60)

        val min = if (counterMin < 10) "0$counterMin" else "$counterMin"
        val sec = if (counterSec < 10) "0$counterSec" else "$counterSec"

        _actualTimeLiveData.postValue("$min:$sec")
    }
}