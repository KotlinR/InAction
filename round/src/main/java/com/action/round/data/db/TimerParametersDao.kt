package com.action.round.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update

@Dao
interface TimerParametersDao {

    @Query("SELECT * FROM timerParameters")
    fun getTimerParameters(): TimerParametersEntity

    @Update
    fun updateTimerParameter(timerParametersEntity: TimerParametersEntity)
}