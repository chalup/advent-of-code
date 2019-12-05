package org.chalup.advent2019

object Day4 {
    fun task1(range: IntRange) = countPasswords(
        range,
        this::isNotDecreasing,
        this::containsDouble
    )

    fun task2(range: IntRange) = countPasswords(
        range,
        this::isNotDecreasing,
        this::containsSeparatedDouble
    )

    private fun countPasswords(range: IntRange, vararg checks: (CharArray) -> Boolean) =
        range
            .asSequence()
            .map { it.toString().toCharArray() }
            .count { digits -> checks.all { check -> check(digits) } }

    fun isNotDecreasing(digits: CharArray): Boolean = digits.pairs().all { (a, b) -> a <= b }
    fun containsDouble(digits: CharArray): Boolean = digits.pairs().any { (a, b) -> a == b }
    fun containsSeparatedDouble(digits: CharArray): Boolean = arrayOf('#', *digits.toTypedArray(), '*')
        .asList()
        .windowed(size = 4)
        .any { (a, b, c, d) -> b == c && a != b && c != d }

    private fun CharArray.pairs() = toList().zipWithNext()
}