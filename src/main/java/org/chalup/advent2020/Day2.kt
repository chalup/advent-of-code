package org.chalup.advent2020

import org.chalup.utils.match

object Day2 {
    fun task1(input: List<String>): Int = input
        .map {
            match<PasswordSpec>(it) {
                pattern("""(\d+)-(\d+) (.): (.*)""") { (low, high, char, password) ->
                    PasswordSpec(
                        char = char.single(),
                        expectedCount = low.toInt()..high.toInt(),
                        password = password
                    )
                }
            }
        }
        .count { spec -> spec.password.count { it == spec.char } in spec.expectedCount }

    fun task2(input: List<String>): Int = input
        .map {
            match<NewPasswordSpec>(it) {
                pattern("""(\d+)-(\d+) (.): (.*)""") { (posA, posB, char, password) ->
                    NewPasswordSpec(
                        char = char.single(),
                        positions = listOf(posA.toInt(), posB.toInt()),
                        password = password
                    )
                }
            }
        }
        .count { spec -> spec.positions.count { spec.password[it - 1] == spec.char } == 1 }

    private data class PasswordSpec(
        val char: Char,
        val expectedCount: IntRange,
        val password: String
    )

    private data class NewPasswordSpec(
        val char: Char,
        val positions: List<Int>,
        val password: String
    )
}