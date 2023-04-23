package com.action.round.data.repos

import com.action.round.data.converters.TrainingConverter
import com.action.round.data.db.TrainingDao
import com.action.round.data.db.TrainingEntity
import com.action.round.data.models.Training
import java.util.concurrent.ExecutorService

class Repository(
    private val trainingDao: TrainingDao,
    private val es: ExecutorService,
    private val trainingConverter: TrainingConverter,
) {

    fun getAll(onTrainingsLoaded: (List<Training>) -> Unit) {
        es.execute {
            onTrainingsLoaded(
                getConvertedTrainings()
            )
        }
    }

    fun save(title: String, exercises: List<String>) {
        es.execute {
            trainingDao.insert(
                TrainingEntity(
                    title = title,
                    exercises = exercises,
                )
            )
        }
    }

    fun update(id: Int, title: String, exercises: List<String>) {
        es.execute {
            trainingDao.updateTrainingById(
                id = id,
                title = title,
                exercises = exercises,
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
            trainingConverter.convert(trainingEntity)
        }
    }
}
