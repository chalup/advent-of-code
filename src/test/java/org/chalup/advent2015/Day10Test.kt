package org.chalup.advent2015

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource


class Day10Test {
    @ParameterizedTest
    @MethodSource("lookAndSay")
    fun `should transform the string`(input: String, output: String) {
        assertThat(Day10.lookAndSay(input)).isEqualTo(output)
    }

    @Suppress("unused")
    companion object {
        @JvmStatic
        fun lookAndSay() = listOf<Arguments>(
            arguments("1", "11"),
            arguments("11", "21"),
            arguments("21", "1211"),
            arguments("1211", "111221"),
            arguments("111221", "312211")
        )
    }
}