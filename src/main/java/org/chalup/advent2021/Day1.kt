package org.chalup.advent2021

object Day1 {
    fun task1(input: List<String>): Int = input
        .asSequence()
        .map { it.toLong() }
        .countIncrements()

    fun task2(input: List<String>): Int = input
        .asSequence()
        .map { it.toLong() }
        .windowed(3) { it.sum() }
        .countIncrements()

    private fun Sequence<Long>.countIncrements() = this
        .zipWithNext { a, b -> b > a }
        .count { it }
}