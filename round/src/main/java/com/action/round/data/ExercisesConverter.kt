package com.action.round.data

import androidx.room.TypeConverter
import java.util.stream.Collectors

class ExercisesConverter {

    @TypeConverter
    fun fromExercises(exercises: List<String>?): String? {
        exercises ?: return null
        return exercises.stream().collect(Collectors.joining("|")) ?: null
    }

    @TypeConverter
    fun toExercises(data: String?): List<String>? {
        data ?: return null
        return listOf(data.split("|").toString())
    }
}