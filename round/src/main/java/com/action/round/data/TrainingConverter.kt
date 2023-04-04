package com.action.round.data

import com.action.round.data.db.TrainingEntity

class TrainingConverter {

    fun convertFromDB(trainingEntity: TrainingEntity): Training {
        return Training(
            id = trainingEntity.id,
            title = trainingEntity.title,
            exercises = trainingEntity.exercises,
        )
    }

    fun convertToDB(training: Training): TrainingEntity {
        return TrainingEntity(
            id = training.id,
            title = training.title,
            exercises = training.exercises,
        )
    }
}