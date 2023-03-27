package com.action.round.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trainings")
data class TrainingEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val title: String,

    val exercises: List<String>,
)
