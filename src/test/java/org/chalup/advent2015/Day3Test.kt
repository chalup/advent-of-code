package org.chalup.advent2015

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class Day3Test {
    @ParameterizedTest
    @MethodSource("routes")
    fun `should calculate number of houses that will get the present`(directions: String, numberOfPresents: Int) {
        assertThat(Day3.countHousesOnRoute(directions)).isEqualTo(numberOfPresents)
    }

    @ParameterizedTest
    @MethodSource("multiplexedRoutes")
    fun `should calculate number of houses that will get the present when robo-santa is operational`(directions: String,
                                                                                                     numberOfPresents: Int) {
        assertThat(Day3.countHousesVisitedBySantaAndRoboSanta(directions)).isEqualTo(numberOfPresents)
    }

    @Suppress("unused")
    companion object {
        @JvmStatic
        fun routes() = listOf<Arguments>(
            arguments(">", 2),
            arguments("^>v<", 4),
            arguments("^v^v^v^v^v", 2)
        )

        @JvmStatic
        fun multiplexedRoutes() = listOf<Arguments>(
            arguments("^v", 3),
            arguments("^>v<", 3),
            arguments("^v^v^v^v^v", 11)
        )
    }
}