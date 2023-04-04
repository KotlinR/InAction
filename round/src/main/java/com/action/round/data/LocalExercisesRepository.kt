package com.action.round.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.ExecutorService

class LocalExercisesRepository(
    private val es: ExecutorService,
) {

    // todo: migrate to coroutines flow
    private val _exercisesLiveData = MutableLiveData<List<Exercise>>(listOf())
    val exercisesLiveData: LiveData<List<Exercise>> = _exercisesLiveData

    fun setExercises(newExercises: List<Exercise>) {
        _exercisesLiveData.value = newExercises
    }

    fun move(from: Int, to: Int) {
        modifyExercises { exercises ->
            val removedExercise = exercises.removeAt(from)
            exercises.add(to, removedExercise)
        }
    }

    fun deleteByPosition(position: Int) {
        modifyExercises { exercises ->
            exercises.removeAt(position)
        }
    }

    fun add() {
        modifyExercises { exercises ->
            exercises.add(Exercise())
        }
    }

    fun clear() {
        _exercisesLiveData.value = emptyList()
    }

    private fun modifyExercises(modify: (MutableList<Exercise>) -> Unit) {
        es.execute {
            val exercises = exercisesLiveData.value.orEmpty().toMutableList()
            modify(exercises)
            _exercisesLiveData.postValue(exercises)
        }
    }
}
