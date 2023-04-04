package com.action.round.data

import androidx.room.TypeConverter

class ExercisesConverter {

    @TypeConverter
    fun fromExercises(exercises: List<String>?): String {
        return exercises.orEmpty().joinToString(separator = "|", transform = { it })
    }

    @TypeConverter
    fun toExercises(data: String?): List<String> {
        return data?.split("|").orEmpty()
    }
}
