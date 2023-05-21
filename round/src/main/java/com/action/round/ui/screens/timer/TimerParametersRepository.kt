package com.action.round.ui.screens.timer

import android.content.Context
import android.content.SharedPreferences

class TimerParametersRepository(
    private val context: Context,
) {

    companion object {
        private const val APP_SP_COUNTDOWN = "countdown"
        private const val APP_SP_ROUND = "round"
        private const val APP_SP_RELAX = "relax"
        private const val APP_SP_PRE_START = "pre_start"
        private const val APP_SP_PRE_STOP = "pre_stop"
        private const val APP_SP_TOTAL_ROUNDS = "total_rounds"

        private const val SAVED_TOTAL_ROUNDS = "saved_total_rounds"
        private const val SAVED_TIME = "saved_time"
    }

    private val timeCountdownSP by lazy {
        context.getSharedPreferences(
            APP_SP_COUNTDOWN,
            Context.MODE_PRIVATE,
        )
    }
    private val timeRoundSP by lazy {
        context.getSharedPreferences(
            APP_SP_ROUND,
            Context.MODE_PRIVATE,
        )
    }
    private val timeRelaxSP by lazy {
        context.getSharedPreferences(
            APP_SP_RELAX,
            Context.MODE_PRIVATE,
        )
    }
    private val timePreStarSP by lazy {
        context.getSharedPreferences(
            APP_SP_PRE_START,
            Context.MODE_PRIVATE,
        )
    }
    private val timePreStopSP by lazy {
        context.getSharedPreferences(
            APP_SP_PRE_STOP,
            Context.MODE_PRIVATE,
        )
    }
    private val totalRoundsSP by lazy {
        context.getSharedPreferences(
            APP_SP_TOTAL_ROUNDS,
            Context.MODE_PRIVATE,
        )
    }

    fun saveTimeCountdown(time: Int) {
        timeCountdownSP.saveSP(time)
    }

    fun saveTimeRound(time: Int) {
        timeRoundSP.saveSP(time)
    }

    fun saveTimeRelax(time: Int) {
        timeRelaxSP.saveSP(time)
    }

    fun saveTimePreStart(time: Int) {
        timePreStarSP.saveSP(time)
    }

    fun saveTimePreStop(time: Int) {
        timePreStopSP.saveSP(time)
    }

    fun saveTotalRounds(totalRounds: Int) {
        totalRoundsSP.saveSP(totalRounds)
    }

    private fun SharedPreferences.saveSP(parameter: Int) {
        this
            .edit()
            .putInt(SAVED_TIME, parameter)
            .apply()
    }

    fun loadTimeCountdown(): Int {
        return timeCountdownSP.getInt(SAVED_TIME, 0)
    }

    fun loadTimeRound(): Int {
        return timeRoundSP.getInt(SAVED_TIME, 180)
    }

    fun loadTimeRelax(): Int {
        return timeRelaxSP.getInt(SAVED_TIME, 60)
    }

    fun loadTimePreStart(): Int {
        return timePreStarSP.getInt(SAVED_TIME, 0)
    }

    fun loadTimePreStop(): Int {
        return timePreStopSP.getInt(SAVED_TIME, 0)
    }

    fun loadTotalRounds(): Int {
        return totalRoundsSP.getInt(SAVED_TOTAL_ROUNDS, 1)
    }
}