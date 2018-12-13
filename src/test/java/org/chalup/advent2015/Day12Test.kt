package org.chalup.advent2015

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class Day12Test {
    @ParameterizedTest
    @MethodSource("jsonData")
    fun `should sum numbers in JSON`(json: String, sum: Int) {
        assertThat(Day12.sumNumbersInJson(json)).isEqualTo(sum)
    }

    @ParameterizedTest
    @MethodSource("jsonDataWithFilter")
    fun `should sum numbers in JSON and apply red filter`(json: String, sum: Int) {
        assertThat(Day12.sumNumbersInJson(json, Day12.isNotRed)).isEqualTo(sum)
    }

    @Suppress("unused")
    companion object {
        @JvmStatic
        fun jsonData() = listOf<Arguments>(
            arguments("""[1,2,3]""", 6),
            arguments("""{"a":2,"b":4}""", 6),
            arguments("""[[[3]]]""", 3),
            arguments("""{"a":{"b":4},"c":-1}""", 3),
            arguments("""{"a":[-1,1]}""", 0),
            arguments("""[-1,{"a":1}]""", 0),
            arguments("""[]""", 0),
            arguments("""{}""", 0)
        )

        @JvmStatic
        fun jsonDataWithFilter() = listOf<Arguments>(
            arguments("""[1,2,3]""", 6),
            arguments("""[1,{"c":"red","b":2},3]""", 4),
            arguments("""{"d":"red","e":[1,2,3,4],"f":5}""", 0),
            arguments("""[1,"red",5]""", 6)
        )
    }
}