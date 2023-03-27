package com.action.round.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TrainingDao {

    @Query("SELECT * FROM trainings")
    fun getAll(): List<TrainingEntity>

//    fun getByAlphabet() : List<TrainingEntity>
//
//    fun getByDateCreated() : List<TrainingEntity>

    @Insert
    fun insert(training: TrainingEntity)

    @Update
    fun update(training: TrainingEntity)

    @Query("DELETE FROM trainings WHERE id = :trainingId")
    fun delete(trainingId: Int)
}