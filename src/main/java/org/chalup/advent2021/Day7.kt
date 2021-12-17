package org.chalup.advent2021

import org.chalup.utils.minMax
import kotlin.math.abs

object Day7 {
    fun task1(input: List<String>): Int {
        val positions = positions(input).sorted()

        val optimalPosition = positions[positions.size / 2]

        return positions.sumOf { abs(it - optimalPosition) }
    }

    fun task2(input: List<String>): Int {
        val positions = positions(input)

        return positions
            .minMax()
            .let { (min, max) -> min..max }
            .minOf { optimalPosition ->
                positions.sumOf { position ->
                    val distance = abs(position - optimalPosition)

                    distance * (distance + 1) / 2
                }
            }
    }

    private fun positions(input: List<String>): List<Int> = input
        .single()
        .splitToSequence(',')
        .map(String::toInt)
        .toList()
}
