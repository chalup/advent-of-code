package org.chalup.advent2022

object Day1 {
    fun task1(input: List<String>): Int = input
        .let(::parse)
        .maxOf { it.sum() }

    fun task2(input: List<String>): Int = input
        .let(::parse)
        .map{ it.sum() }
        .sortedDescending()
        .take(3)
        .sum()

    private fun parse(input: List<String>): List<List<Int>> = buildList<MutableList<Int>> {
        add(mutableListOf())

        for (line in input) {
            if (line.isEmpty()) {
                add(mutableListOf())
            } else {
                last().add(line.toInt())
            }
        }
    }
}