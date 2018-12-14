package org.chalup.advent2018

import com.google.common.truth.Truth.assertThat
import org.chalup.advent2018.Day14.getNumberOfRecipesPrecedingTheResults
import org.chalup.advent2018.Day14.getScoresAfterSomeTrials
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class Day14Test {
    @ParameterizedTest
    @MethodSource("knownChecksums")
    fun `should calculate a checksum of 10 recipes after specified number of trials`(trials: Int, checksum: String) {
        assertThat(Day14.generateRecipes().getScoresAfterSomeTrials(trials, 10).joinToString("")).isEqualTo(checksum)
    }

    @ParameterizedTest
    @MethodSource("part2")
    fun `should calculate a number of recipes preceding a given scores`(scores: String, numberOfRecipes: Int) {
        assertThat(Day14.generateRecipes().getNumberOfRecipesPrecedingTheResults(scores)).isEqualTo(numberOfRecipes)
    }

    @Suppress("unused")
    companion object {
        @JvmStatic
        fun knownChecksums() = listOf<Arguments>(
            Arguments.arguments(9, "5158916779"),
            Arguments.arguments(5, "0124515891"),
            Arguments.arguments(18, "9251071085"),
            Arguments.arguments(2018, "5941429882")
        )

        @JvmStatic
        fun part2() = listOf<Arguments>(
            Arguments.arguments("51589", 9),
            Arguments.arguments("01245", 5),
            Arguments.arguments("92510", 18),
            Arguments.arguments("59414", 2018)
        )
    }
}