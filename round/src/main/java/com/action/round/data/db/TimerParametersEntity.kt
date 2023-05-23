package com.action.round.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "timerParameters"
)
data class TimerParametersEntity(
    @PrimaryKey
    val countdown: Int = 0,
    val round: Int = 0,
    val relax: Int = 0,
    val preStart: Int = 0,
    val preStop: Int = 0,
    val totalRounds: Int = 0,
)