package org.chalup.advent2021

import kotlin.math.abs

object Day7 {
    fun task1(input: String): Int {
        val positions = positions(input).sorted()

        val optimalPosition = positions[positions.size / 2]

        return positions.sumOf { abs(it - optimalPosition) }
    }

    fun task2(input: String): Int {
        val positions = positions(input)

        return positions
            .minMaxRange()
            .minOf { optimalPosition ->
                positions.sumOf { position ->
                    val distance = abs(position - optimalPosition)

                    distance * (distance + 1) / 2
                }
            }
    }

    private fun positions(input: String): List<Int> = input
        .splitToSequence(',')
        .map(String::toInt)
        .toList()
}

private fun Iterable<Int>.minMaxRange(): IntRange {
    var min = Int.MAX_VALUE
    var max = Int.MIN_VALUE

    forEach { i ->
        min = minOf(min, i)
        max = maxOf(max, i)
    }

    return min..max
}

