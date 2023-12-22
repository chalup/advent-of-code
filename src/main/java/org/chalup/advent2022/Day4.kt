package org.chalup.advent2022

import org.chalup.utils.contains
import org.chalup.utils.intersects

object Day4 {
    fun task1(input: List<String>) = input
        .map { parsePair(it) }
        .count { (first, second) ->
            first in second || second in first
        }

    fun task2(input: List<String>) = input
        .map { parsePair(it) }
        .count { (first, second) -> first intersects second }

    private fun parseRange(input: String): LongRange = input
        .splitToSequence('-')
        .map { it.toLong() }
        .iterator()
        .let { it.next()..it.next() }

    private fun parsePair(input: String): Pair<LongRange, LongRange> = input
        .splitToSequence(',')
        .map { parseRange(it) }
        .iterator()
        .let { it.next() to it.next() }
}