package org.chalup.advent2016

import org.chalup.utils.except
import org.chalup.utils.size

object Day20 {
    fun task1(input: List<String>) = allowedRanges(input).minBy { it.first }.first
    fun task2(input: List<String>) = allowedRanges(input).sumOf { it.size() }

    private fun allowedRanges(input: List<String>): List<LongRange> {
        val blockedRanges = input.map { it.split("-").map(String::toLong).let { (a, b) -> a..b } }

        return blockedRanges
            .fold(listOf(0L..4294967295L)) { allowedRanges, blockedRange ->
                allowedRanges.flatMap { it except blockedRange }
            }
    }
}
