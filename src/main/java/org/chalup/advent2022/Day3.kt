package org.chalup.advent2022

object Day3 {
    fun task1(input: List<String>): Int = input
        .map { rucksack ->
            val firstHalf = rucksack.take(rucksack.length / 2).toSet()
            val secondHalf = rucksack.drop(rucksack.length / 2).toSet()
            (firstHalf intersect secondHalf).single()
        }
        .sumOf(::itemPriority)

    fun task2(input: List<String>) = input
        .chunked(3) { group ->
            group
                .map { it.toSet() }
                .reduce { acc, chars -> acc intersect chars }
                .single()
        }
        .sumOf(::itemPriority)

    private fun itemPriority(item: Char) = when (item) {
        in 'a'..'z' -> item - 'a' + 1
        in 'A'..'Z' -> item - 'A' + 27
        else -> throw IllegalArgumentException("Unexpected rucksack item $item")
    }
}