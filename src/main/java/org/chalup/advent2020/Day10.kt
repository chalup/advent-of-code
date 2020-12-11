package org.chalup.advent2020

object Day10 {
    fun task1(input: List<String>): Int = input
        .map(String::toInt)
        .sorted()
        .let { adapters ->
            sequence {
                yield(0)
                yieldAll(adapters)
                yield(adapters.last() + 3)
            }
        }
        .zipWithNext { a, b -> b - a }
        .groupingBy { it }.eachCount()
        .let { it.getOrDefault(1, 0) * it.getOrDefault(3, 0) }
}
