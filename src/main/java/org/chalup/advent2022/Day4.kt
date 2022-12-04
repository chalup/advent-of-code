package org.chalup.advent2022

object Day4 {
    fun task1(input: List<String>) = input
        .map { parsePair(it) }
        .count { (first, second) ->
            first in second || second in first
        }

    fun task2(input: List<String>) = input
        .map { parsePair(it) }
        .count { (first, second) ->
            first overlaps second || second overlaps first
        }

    private operator fun IntRange.contains(other: IntRange): Boolean =
        other.first >= this.first && other.last <= this.last

    private infix fun IntRange.overlaps(other: IntRange): Boolean =
        other.first in this || other.last in this

    private fun parseRange(input: String): IntRange = input
        .splitToSequence('-')
        .map { it.toInt() }
        .iterator()
        .let { it.next()..it.next() }

    private fun parsePair(input: String): Pair<IntRange, IntRange> = input
        .splitToSequence(',')
        .map { parseRange(it) }
        .iterator()
        .let { it.next() to it.next() }
}