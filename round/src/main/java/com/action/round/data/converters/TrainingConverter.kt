package com.action.round.data.converters

import com.action.round.data.db.TrainingEntity
import com.action.round.data.models.Exercise
import com.action.round.data.models.Training

class TrainingConverter {

    fun convert(trainingEntity: TrainingEntity): Training {
        return Training(
            id = trainingEntity.id,
            title = trainingEntity.title,
            exercises = trainingEntity.exercises.map { Exercise(description = it) },
        )
    }
}
