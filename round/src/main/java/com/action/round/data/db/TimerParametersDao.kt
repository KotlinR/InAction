package com.action.round.data.db

import androidx.room.*

@Dao
interface TimerParametersDao {

    @Query("SELECT * FROM timerParameters")
    fun getTimerParameters(): TimerParametersEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun createPrimaryTimerParameters(timerParametersEntity: TimerParametersEntity)

    @Update
    fun updateTimerParameter(timerParametersEntity: TimerParametersEntity)
}