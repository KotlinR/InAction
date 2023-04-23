package com.action.round.data.converters

import androidx.room.TypeConverter

class ExercisesConverter {

    private companion object {
        const val SEPARATOR = "#"
    }

    @TypeConverter
    fun fromExercises(exercises: List<String>?): String {
        return exercises.orEmpty().joinToString(separator = SEPARATOR, transform = { it })
    }

    @TypeConverter
    fun toExercises(data: String?): List<String> {
        return data?.split(SEPARATOR).orEmpty()
    }
}
