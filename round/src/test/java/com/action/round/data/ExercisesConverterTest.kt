package com.action.round.data

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.properties.Delegates

class ExercisesConverterTest {

    private var sut: ExercisesConverter by Delegates.notNull()

    @Before
    fun setUp() {
        sut = ExercisesConverter()
    }

    @Test
    fun fromExercises_listOfStrings_assertValidResultString() {
        val exercises = listOf(
            "exercise_1",
            "exercise_2",
            "exercise_3",
        )
        val actual = sut.fromExercises(exercises)
        val expected = "exercise_1#exercise_2#exercise_3"
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun toExercises_string_assertValidListOfExercises() {
        val exercisesStr = "exercise_1#exercise_2#exercise_3"
        val actual = sut.toExercises(exercisesStr)
        val expected = listOf(
            "exercise_1",
            "exercise_2",
            "exercise_3",
        )
        Assert.assertEquals(expected, actual)
    }
}
