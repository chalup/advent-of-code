package org.chalup.advent2018

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class Day9Test {
    @ParameterizedTest
    @MethodSource("gameParams")
    fun `should calculate high score`(players: Int, marbles: Long, highScore: Long) {
        assertThat(Day9.calculateHighScore(players, marbles)).isEqualTo(highScore)
    }

    @Suppress("unused")
    companion object {
        @JvmStatic
        fun gameParams() = listOf<Arguments>(
            arguments(10, 1618, 8317),
            arguments(13, 7999, 146373),
            arguments(17, 1104, 2764),
            arguments(21, 6111, 54718),
            arguments(30, 5807, 37305)
        )
    }
}