package org.chalup.advent2015

import com.google.common.truth.Truth.assertThat
import org.chalup.advent2015.Day5.isNice
import org.chalup.advent2015.Day5.isReallyNice
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource


class Day5Test {
    @ParameterizedTest
    @MethodSource("strings")
    fun `should determine if string is nice or naughty`(string: String, isNice: Boolean) {
        assertThat(string.isNice()).isEqualTo(isNice)
    }

    @ParameterizedTest
    @MethodSource("reallyNiceStrings")
    fun `should determine if string is really nice or really naughty`(string: String, isReallyNice: Boolean) {
        assertThat(string.isReallyNice()).isEqualTo(isReallyNice)
    }

    @Suppress("unused")
    companion object {
        @JvmStatic
        fun strings() = listOf<Arguments>(
            arguments("ugknbfddgicrmopn", true),
            arguments("aaa", true),
            arguments("jchzalrnumimnmhp", false),
            arguments("haegwjzuvuyypxyu", false),
            arguments("dvszwmarrgswjxmb", false)
        )

        @JvmStatic
        fun reallyNiceStrings() = listOf<Arguments>(
            arguments("qjhvhtzxzqqjkmpb", true),
            arguments("xxyxx", true),
            arguments("uurcxstgmygtbstg", false),
            arguments("ieodomkazucvgmuy", false)
        )
    }
}