package org.chalup.advent2015

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class Day8Test {
    @ParameterizedTest
    @MethodSource("unescaping")
    fun `should unescape strings`(input: String, unescaped: String) =
        assertThat(Day8.unescape(input)).isEqualTo(unescaped)

    @ParameterizedTest
    @MethodSource("escaping")
    fun `should escape strings`(input: String, escaped: String) =
        assertThat(Day8.escape(input)).isEqualTo(escaped)

    @Suppress("unused")
    companion object {
        @JvmStatic
        fun unescaping() = listOf<Arguments>(
            Arguments.arguments("""""""", ""),
            Arguments.arguments(""""abc"""", "abc"),
            Arguments.arguments(""""aaa\"aaa"""", """aaa"aaa"""),
            Arguments.arguments(""""abc"""", "abc"),
            Arguments.arguments(""""\x27"""", "'")
        )

        @JvmStatic
        fun escaping() = listOf<Arguments>(
            Arguments.arguments("""""""", """"\"\"""""),
            Arguments.arguments(""""abc"""", """"\"abc\"""""),
            Arguments.arguments(""""aaa\"aaa"""", """"\"aaa\\\"aaa\"""""),
            Arguments.arguments(""""\x27"""", """"\"\\x27\""""")
        )
    }
}