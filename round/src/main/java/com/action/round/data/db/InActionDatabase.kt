package com.action.round.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.action.round.data.converters.ExercisesConverter


@Database(entities = [TrainingEntity::class], version = 1)
@TypeConverters(ExercisesConverter::class)
abstract class InActionDatabase : RoomDatabase() {
    abstract fun trainingDao(): TrainingDao
}
