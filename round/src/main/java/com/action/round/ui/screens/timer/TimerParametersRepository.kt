package com.action.round.ui.screens.timer

import android.content.Context

class TimerParametersRepository(
    private val context: Context,
) {

    companion object {
        private const val APP_SP_TIME_PARAMETERS = "time_parameters"

        private const val COUNTDOWN = "countdown"
        private const val ROUND = "round"
        private const val RELAX = "relax"
        private const val PRE_START = "pre_start"
        private const val PRE_STOP = "pre_stop"
        private const val TOTAL_ROUNDS = "total_rounds"
    }

    private val timeParametersSP by lazy {
        context.getSharedPreferences(
            APP_SP_TIME_PARAMETERS,
            Context.MODE_PRIVATE,
        )
    }

    fun saveTimeCountdown(time: Int) {
        saveTimerParameterSP(COUNTDOWN, time)
    }

    fun saveTimeRound(time: Int) {
        saveTimerParameterSP(ROUND, time)
    }

    fun saveTimeRelax(time: Int) {
        saveTimerParameterSP(RELAX, time)
    }

    fun saveTimePreStart(time: Int) {
        saveTimerParameterSP(PRE_START, time)
    }

    fun saveTimePreStop(time: Int) {
        saveTimerParameterSP(PRE_STOP, time)
    }

    fun saveTotalRounds(totalRounds: Int) {
        saveTimerParameterSP(TOTAL_ROUNDS, totalRounds)
    }

    private fun saveTimerParameterSP(key: String, parameter: Int) {
        timeParametersSP
            .edit()
            .putInt(key, parameter)
            .apply()
    }

    fun loadTimeCountdown(): Int {
        return timeParametersSP.getInt(COUNTDOWN, 0)
    }

    fun loadTimeRound(): Int {
        return timeParametersSP.getInt(ROUND, 180)
    }

    fun loadTimeRelax(): Int {
        return timeParametersSP.getInt(RELAX, 60)
    }

    fun loadTimePreStart(): Int {
        return timeParametersSP.getInt(PRE_START, 0)
    }

    fun loadTimePreStop(): Int {
        return timeParametersSP.getInt(PRE_STOP, 0)
    }

    fun loadTotalRounds(): Int {
        return timeParametersSP.getInt(TOTAL_ROUNDS, 1)
    }
}