package org.chalup.advent2022

object Day25 {
    fun task1(input: List<String>) = input
        .map { it.reversed() }
        .let { reversedSnafuNumbers ->
            var digit = 0
            var carry = 0

            val maxDigits = reversedSnafuNumbers.maxOf { it.length }

            buildString {
                while (true) {
                    val sum = carry + reversedSnafuNumbers.sumOf { it.getOrNull(digit)?.let(::snafuCharToInt) ?: 0 }
                    if (sum == 0 && digit >= maxDigits) break
                    val (char, newCarry) = intToSnafuCharAndCarry(sum)

                    append(char)
                    carry = newCarry
                    digit++
                }
            }
        }
        .reversed()

    private fun intToSnafuCharAndCarry(int: Int): Pair<Char, Int> {
        var carry = 0
        var number = int
        while (number < -2) {
            number += 5
            carry -= 1
        }

        while (number > +2) {
            number -= 5
            carry += 1
        }

        val char = when (number) {
            -2 -> '='
            -1 -> '-'
            0 -> '0'
            1 -> '1'
            2 -> '2'
            else -> throw IllegalArgumentException("FAIL: $int converted to $number and $carry")
        }

        return char to carry
    }

    private fun snafuCharToInt(char: Char): Int = when (char) {
        '=' -> -2
        '-' -> -1
        '0' -> 0
        '1' -> 1
        '2' -> 2
        else -> throw IllegalArgumentException("Unexpected SNAFU char '$char'")
    }
}