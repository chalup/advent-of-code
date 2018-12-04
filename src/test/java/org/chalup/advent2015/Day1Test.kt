package org.chalup.advent2015

import com.google.common.truth.Truth.assertThat
import org.chalup.advent2015.Day1.basementDirectionsIndex
import org.chalup.advent2015.Day1.findFloor
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class Day1Test {
    @ParameterizedTest
    @MethodSource("floorDirections")
    fun `should calculate correct floor number`(directions: String, floor: Int) {
        assertThat(findFloor(directions)).isEqualTo(floor)
    }

    @ParameterizedTest
    @MethodSource("basementLevelData")
    fun `should find the index of direction leading to the basement`(directions: String, index: Int) {
        assertThat(basementDirectionsIndex(directions)).isEqualTo(index)
    }

    @Suppress("unused")
    companion object {
        @JvmStatic
        fun floorDirections() = listOf<Arguments>(
            arguments("(())", 0),
            arguments("()()", 0),
            arguments("(((", 3),
            arguments("(()(()(", 3),
            arguments("))(((((", 3),
            arguments("())", -1),
            arguments("))(", -1),
            arguments(")))", -3),
            arguments(")())())", -3)
        )

        @JvmStatic
        fun basementLevelData() = listOf<Arguments>(
            arguments(")", 1),
            arguments("()())", 5)
        )
    }
}