package org.chalup.advent2019

import com.google.common.truth.Truth
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class Day1Test {
    @ParameterizedTest
    @MethodSource("fuelRequirementData")
    fun `should calculate fuel requirement`(mass: Int, expectedResult: Int) {
        Truth.assertThat(Day1.fuelRequirement(mass)).isEqualTo(expectedResult)
    }

    @ParameterizedTest
    @MethodSource("totalFuelRequirementData")
    fun `should calculate total fuel requirement`(moduleMass: Int, expectedResult: Int) {
        Truth.assertThat(Day1.totalFuelRequirement(moduleMass)).isEqualTo(expectedResult)
    }

    companion object {
        @JvmStatic
        fun fuelRequirementData() = listOf<Arguments>(
            Arguments.arguments(12, 2),
            Arguments.arguments(14, 2),
            Arguments.arguments(1969, 654),
            Arguments.arguments(100756, 33583)
        )

        @JvmStatic
        fun totalFuelRequirementData() = listOf<Arguments>(
            Arguments.arguments(14, 2),
            Arguments.arguments(1969, 966),
            Arguments.arguments(100756, 50346)
        )
    }
}