package org.chalup.advent2018

import com.google.common.truth.Truth
import org.chalup.advent2018.Day1.calculateFinalFrequency
import org.chalup.advent2018.Day1.detectCycle
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class Day1Test {
    @ParameterizedTest
    @MethodSource("testData")
    fun `should report correct frequency`(input: List<String>, expectedResult: Int) {
        Truth.assertThat(calculateFinalFrequency(input)).isEqualTo(expectedResult)
    }

    @ParameterizedTest
    @MethodSource("part2TestData")
    fun `should detect correct frequency cycle`(input: List<String>, expectedResult: Int) {
        Truth.assertThat(detectCycle(input)).isEqualTo(expectedResult)
    }

    companion object {
        @JvmStatic
        fun testData() = listOf<Arguments>(
            arguments(listOf("+1", "+1", "+1"), 3),
            arguments(listOf("+1", "+1", "-2"), 0),
            arguments(listOf("-1", "-2", "-3"), -6)
        )

        @JvmStatic
        fun part2TestData() = listOf<Arguments>(
            arguments(listOf("+1", "-1"), 0),
            arguments(listOf("+3", "+3", "+4", "-2", "-4"), 10),
            arguments(listOf("-6", "+3", "+8", "+5", "-6"), 5),
            arguments(listOf("+7", "+7", "-2", "-7", "-4"), 14)
        )
    }
}