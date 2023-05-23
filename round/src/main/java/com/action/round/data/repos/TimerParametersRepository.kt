package com.action.round.data.repos

import com.action.round.data.converters.TimerParametersConverter
import com.action.round.data.db.TimerParametersDao
import com.action.round.data.db.TimerParametersEntity
import com.action.round.data.models.TimerParameters
import java.util.concurrent.ExecutorService

class TimerParametersRepository(
    private val timerParametersDao: TimerParametersDao,
    private val es: ExecutorService,
    private val timerParametersConverter: TimerParametersConverter,
) {

    fun getTimerParameters(onTrainingParameters: (TimerParameters?) -> Unit) {
        es.execute {
            val parameters = timerParametersDao.getTimerParameters()
            onTrainingParameters(
                timerParametersConverter.converter(parameters)
            )
        }
    }

    fun updateTimerParameters(timerParameters: TimerParameters) {
        es.execute {
            timerParametersDao.updateTimerParameter(
                TimerParametersEntity(
                    countdown = timerParameters.countdown,
                    round = timerParameters.round,
                    relax = timerParameters.relax,
                    preStart = timerParameters.preStart,
                    preStop = timerParameters.preStop,
                    totalRounds = timerParameters.totalRounds,
                )
            )
        }
    }
}