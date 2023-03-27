package com.action.round.data

import com.action.round.data.db.TrainingDao
import java.util.concurrent.ExecutorService

class Repository(
    private val trainingDao: TrainingDao,
    private val es: ExecutorService,
    private val converter: TrainingConverter,
) {

    fun getAll(onTrainingsLoaded: (List<Training>) -> Unit) {
        es.execute {
            onTrainingsLoaded(
                getConvertedTrainings()
            )
        }
    }

    fun delete(training: Training, onTrainingDeleted: (List<Training>) -> Unit) {
        es.execute {
            trainingDao.delete(training.id)
            onTrainingDeleted(
                getConvertedTrainings()
            )
        }
    }

    private fun getConvertedTrainings(): List<Training> {
        return trainingDao.getAll().map { trainingEntity ->
            converter.convert(trainingEntity)
        }
    }
}