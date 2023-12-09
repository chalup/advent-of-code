package org.chalup.advent2023

object Day9 {
    fun task1(input: List<String>): Long = input
        .let(::parseSequences)
        .sumOf { predictNextNumber(it) }

    fun task2(input: List<String>): Long = input
        .let(::parseSequences)
        .sumOf { predictEarlierNumber(it) }
}

private fun predictNextNumber(initialSequence: List<Long>): Long =
    sequences(initialSequence).sumOf { it.last() }

private fun predictEarlierNumber(initialSequence: List<Long>): Long =
    sequences(initialSequence)
        .map { it.first() }
        .toList()
        .reversed()
        .fold(0L) { acc, next -> next - acc }

private fun parseSequences(input: List<String>) = input
    .map { it.split(' ').map(String::toLong) }

private fun sequences(initialSequence: List<Long>) = generateSequence(initialSequence) { numbers ->
    numbers
        .takeIf { it.size > 1 }
        ?.takeUnless { list -> list.all { it == 0L } }
        ?.let { it.zipWithNext { a, b -> b - a } }
}