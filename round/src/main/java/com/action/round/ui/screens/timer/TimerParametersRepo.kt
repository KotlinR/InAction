package com.action.round.ui.screens.timer

import android.content.Context

class TimerParametersRepo(
    private val context: Context,
) {

    private companion object {
        const val APP_SP_TIME_PARAMETERS = "time_parameters"

        const val COUNTDOWN = "countdown"
        const val ROUND = "round"
        const val RELAX = "relax"
        const val PRE_START = "pre_start"
        const val PRE_STOP = "pre_stop"
        const val TOTAL_ROUNDS = "total_rounds"
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