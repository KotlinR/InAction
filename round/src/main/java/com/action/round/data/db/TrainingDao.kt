package com.action.round.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TrainingDao {

    @Query("SELECT * FROM trainings")
    fun getAll(): List<TrainingEntity>

//    fun getAllSortedASC() : List<TrainingEntity>
//
//    fun getAllSortedByDate() : List<TrainingEntity>

    @Insert
    fun insert(training: TrainingEntity)

    @Query("DELETE FROM trainings WHERE id = :trainingId")
    fun delete(trainingId: Int)

    @Query("UPDATE trainings SET title = :title, exercises = :exercises WHERE id = :id")
    fun updateTrainingById(id: Int, title: String, exercises: List<String>)
}
