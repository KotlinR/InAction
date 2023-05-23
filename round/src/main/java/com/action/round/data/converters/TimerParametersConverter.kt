package com.action.round.data.converters

import com.action.round.data.db.TimerParametersEntity
import com.action.round.data.models.TimerParameters

class TimerParametersConverter {

    fun converter(timerParametersEntity: TimerParametersEntity?): TimerParameters? {
        return if (timerParametersEntity != null) TimerParameters(
            countdown = timerParametersEntity.countdown,
            round = timerParametersEntity.round,
            relax = timerParametersEntity.relax,
            preStart = timerParametersEntity.preStart,
            preStop = timerParametersEntity.preStop,
            totalRounds = timerParametersEntity.totalRounds,
        ) else null
    }
}