package com.action.round.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "timerParameters",
)
data class TimerParametersEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val countdown: Int = 0,
    val round: Int = 180,
    val relax: Int = 60,
    val preStart: Int = 0,
    val preStop: Int = 0,
    val totalRounds: Int = 1,
)
