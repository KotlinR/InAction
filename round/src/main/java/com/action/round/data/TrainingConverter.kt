package com.action.round.data

import com.action.round.data.db.TrainingEntity

class TrainingConverter {

    fun convert(trainingEntity: TrainingEntity): Training {
        return Training(
            id = trainingEntity.id,
            title = trainingEntity.title,
            exercises = trainingEntity.exercises.map { Exercise(description = it) },
        )
    }
}
