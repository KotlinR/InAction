package com.action.round.data

import org.junit.Assert
import org.junit.Test

class IdGeneratorTest {

    @Test
    fun getId_assertIncrementationOfIds() {
        val actual = List(5) {
            IdGenerator.id
        }
        val expected = listOf(1, 2, 3, 4, 5)
        Assert.assertEquals(expected, actual)
    }
}
