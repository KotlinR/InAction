package com.action.round.utills

import org.junit.Assert
import org.junit.Test

class ListsKtTest {

    @Test
    fun findAndUpdate() {
        val actual = mutableListOf("1", "2", "3", "4").apply {
            findAndUpdate(
                predicate = { it == "2" },
                modify = { it + it },
            )
        }
        val expected = mutableListOf("1", "22", "3", "4")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun swap_elementsIntoTheMiddle_assertCorrectSwap() {
        val actual = mutableListOf("1", "2", "3", "4").apply {
            swap(1, 2)
        }
        val expected = mutableListOf("1", "3", "2", "4")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun swap_elementsIntoTheEnd_assertCorrectSwap() {
        val actual = mutableListOf("1", "2", "3", "4").apply {
            swap(2, 3)
        }
        val expected = mutableListOf("1", "2", "4", "3")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun swap_elementsIntoTheBeginning_assertCorrectSwap() {
        val actual = mutableListOf("1", "2", "3", "4").apply {
            swap(0, 1)
        }
        val expected = mutableListOf("2", "1", "3", "4")
        Assert.assertEquals(expected, actual)
    }
}
