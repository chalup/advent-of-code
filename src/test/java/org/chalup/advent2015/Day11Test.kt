package org.chalup.advent2015


import com.google.common.truth.Truth.assertThat
import org.chalup.advent2015.Day11.isGoodPassword
import org.chalup.advent2015.Day11.next
import org.chalup.advent2015.Day11.nextGoodPassword
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource


class Day11Test {
    @ParameterizedTest
    @MethodSource("passwordCheck")
    fun `should check the password`(password: String, isGood: Boolean) {
        assertThat(password.isGoodPassword()).isEqualTo(isGood)
    }

    @ParameterizedTest
    @MethodSource("stringProgression")
    fun `should properly increment the string`(password: String, nextPassword: String) {
        assertThat(password.next()).isEqualTo(nextPassword)
    }

    @ParameterizedTest
    @MethodSource("nextPassword")
    fun `should find the next password`(password: String, nextPassword: String) {
        assertThat(password.nextGoodPassword()).isEqualTo(nextPassword)
    }

    @Suppress("unused")
    companion object {
        @Suppress("SpellCheckingInspection")
        @JvmStatic
        fun passwordCheck() = listOf<Arguments>(
            arguments("hijklmmn", false),
            arguments("abbceffg", false),
            arguments("abbcegjk", false),
            arguments("abcdefgh", false),
            arguments("abcdffaa", true),
            arguments("ghijklmn", false),
            arguments("ghjaabcc", true)
        )

        @Suppress("SpellCheckingInspection")
        @JvmStatic
        fun stringProgression() = listOf<Arguments>(
            arguments("a", "b"),
            arguments("aa", "ab"),
            arguments("az", "ba"),
            arguments("zz", "aaa")
        )

        @Suppress("SpellCheckingInspection")
        @JvmStatic
        fun nextPassword() = listOf<Arguments>(
            arguments("abcdefgh", "abcdffaa"),
            arguments("ghijklmn", "ghjaabcc")
        )
    }
}
