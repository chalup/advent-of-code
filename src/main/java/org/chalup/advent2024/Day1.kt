package org.chalup.advent2024

import org.chalup.utils.Counter
import kotlin.math.absoluteValue

object Day1 {
    fun task1(input: List<String>): Int {
        val (left, right) = columns(input)

        return left.sorted()
            .zip(right.sorted()) { l, r -> (l - r).absoluteValue }
            .sum()
    }

    fun task2(input: List<String>): Int {
        val (left, right) = columns(input)

        val rightCounts = Counter(right)

        return left.sumOf { it * (rightCounts[it] ?: 0) }
    }

    private fun columns(input: List<String>): Pair<List<Int>, List<Int>> {
        val pairs = input.map { line -> line.split("\\s+".toRegex()).map { it.toInt() } }

        val left = pairs.map { (l, _) -> l }
        val right = pairs.map { (_, r) -> r }

        return left to right
    }
}