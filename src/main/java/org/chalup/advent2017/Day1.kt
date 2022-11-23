package org.chalup.advent2017

object Day1 {
    fun task1(input: List<String>) = input
        .first()
        .toList()
        .let { it.asSequence() + it.first() }
        .map { it - '0' }
        .zipWithNext()
        .filter { (a, b) -> a == b }
        .sumOf { (a, _) -> a }

    fun task2(input: List<String>) = input
        .first()
        .toList()
        .map { it - '0' }
        .let { it.take(it.size / 2) to it.drop(it.size / 2) }
        .let { (firstHalf, secondHalf) -> firstHalf.zip(secondHalf) }
        .sumOf { (a, b) -> (a + b).takeIf { a == b } ?: 0 }
}