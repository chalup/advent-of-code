package org.chalup.advent2018

import com.google.common.truth.Truth
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class Day5Test {
    @ParameterizedTest
    @MethodSource("polymersReductionData")
    fun `should reduce the polymer`(polymer: String, reducedPolymer: String) {
        Truth.assertThat(Day5.reduce(polymer)).isEqualTo(reducedPolymer)
    }

    @Test
    fun `should find the correct element to remove`() {
        Truth.assertThat(Day5.minimalImprovedPolymerLength("dabAcCaCBAcCcaDA")).isEqualTo(4)
    }

    companion object {
        @JvmStatic
        fun polymersReductionData() = listOf<Arguments>(
            arguments("dabAcCaCBAcCcaDA", "dabCBAcaDA"),
            arguments("aA", ""),
            arguments("abBA", ""),
            arguments("abAB", "abAB"),
            arguments("aabAAB", "aabAAB")
        )
    }
}