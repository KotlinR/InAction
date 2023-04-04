package com.action.round.ui.screens.training

class LocalExercisesRepository {

    private val exercises: MutableList<String> = mutableListOf()

    fun getExercises(): List<String> = exercises

    fun setExercises(newExercises: List<String>) {
        exercises.clear()
        exercises.addAll(newExercises)
    }

    fun move(from: Int, to: Int) {
        val removedExercise = exercises.removeAt(from)
        exercises.add(to, removedExercise)
    }

    fun delete(position: Int) {
        exercises.removeAt(position)
    }

    fun update() {

    }

    fun add() {
        exercises.add("")
    }
}