package org.chalup.advent2018

import com.google.common.truth.Truth
import org.chalup.advent2018.Day2.BoxIdCategory.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class Day2Test {
    @ParameterizedTest
    @MethodSource("categorizeTestData")
    fun `should property categorize box`(id: String, categories: List<Day2.BoxIdCategory>) {
        Truth.assertThat(Day2.categorizeBoxId(id)).containsExactlyElementsIn(categories)
    }

    @Test
    fun `should calculate correct checksum`() {
        val input = listOf("abcdef",
                           "bababc",
                           "abbcde",
                           "abcccd",
                           "aabcdd",
                           "abcdee",
                           "ababab")

        Truth.assertThat(Day2.checksum(input)).isEqualTo(12)
    }

    companion object {
        @JvmStatic
        fun categorizeTestData() = listOf<Arguments>(
            Arguments.arguments("abcdef", emptyList<Day2.BoxIdCategory>()),
            Arguments.arguments("ababab", listOf(TRIPLE)),
            Arguments.arguments("bababc", listOf(DOUBLE, TRIPLE)),
            Arguments.arguments("abbcde", listOf(DOUBLE))
        )
    }

}