package org.chalup.advent2020

object Day10 {
    fun task1(input: List<String>): Int = adapters(input)
        .zipWithNext { a, b -> b - a }
        .groupingBy { it }.eachCount()
        .let { it.getOrDefault(1, 0) * it.getOrDefault(3, 0) }

    fun task2(input: List<String>): Long = adapters(input)
        .toSet()
        .let { adapters ->
            val cache = mutableMapOf<Long, Long>()
            fun arrangements(startingAdapter: Long): Long = cache.getOrPut(startingAdapter) {
                if (startingAdapter == adapters.last()) {
                    1
                } else {
                    adapters
                        .filter { it - startingAdapter in 1..3 }
                        .sumOf { arrangements(it) }
                }
            }

            arrangements(0L)
        }

    private fun adapters(input: List<String>) = input
        .map(String::toLong)
        .sorted()
        .let { adapters ->
            sequence {
                yield(0)
                yieldAll(adapters)
                yield(adapters.last() + 3)
            }
        }
}
