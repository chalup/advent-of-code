package org.chalup.advent2017

object Day13 {
    fun task1(input: List<String>): Int = getTripSeverity(parseRanges(input)) ?: 0

    fun task2(input: List<String>): Int {
        val ranges = parseRanges(input)

        return generateSequence(0) { it + 1 }
            .dropWhile { getTripSeverity(ranges, it) != null }
            .first()
    }

    private fun parseRanges(input: List<String>) = input
        .associate { it.split(": ").map(String::toInt).let { (depth, range) -> depth to range } }

    private fun getTripSeverity(ranges: Map<Int, Int>, initialDelay: Int = 0): Int? {
        return ranges
            .filter { (depth, range) -> (initialDelay + depth) % ((range - 1) * 2) == 0 }
            .entries
            .takeIf { it.isNotEmpty() }
            ?.sumOf { (depth, range) -> depth * range }
    }
}