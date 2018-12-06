package org.chalup.advent2015

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class Day4Test {
    @ParameterizedTest
    @MethodSource("secrets")
    fun `should calculate correct number for a given secret key`(secret: String, difficulty: Int, number: Int) {
        assertThat(Day4.mineAdventCoin(secret, difficulty)).isEqualTo(number)
    }

    @Suppress("unused")
    companion object {
        @JvmStatic
        fun secrets() = listOf<Arguments>(
            arguments("abcdef", 5, 609043),
            arguments("pqrstuv", 5, 1048970)
        )
    }
}