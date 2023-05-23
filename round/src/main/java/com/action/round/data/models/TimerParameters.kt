package com.action.round.data.models

data class TimerParameters(
    var countdown: Int = 0,
    var round: Int = 180,
    var relax: Int = 60,
    var preStart: Int = 0,
    var preStop: Int = 0,
    var totalRounds: Int = 5,
)
