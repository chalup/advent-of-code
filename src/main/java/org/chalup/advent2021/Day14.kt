package org.chalup.advent2021

import org.chalup.utils.minMaxLong

object Day14 {
    fun task1(input: List<String>): Long = input
        .let(this::parse)
        .let { go(it, n = 10) }

    fun task2(input: List<String>): Long = input
        .let(this::parse)
        .let { go(it, n = 40) }

    private fun go(polymerization: Polymerization, n: Int): Long {
        val (initialPolymer, rules) = polymerization

        val initialPairCounts = initialPolymer
            .zipWithNext { a, b -> "$a$b" }
            .groupingBy { it }
            .eachCount()
            .mapValues { (_, count) -> count.toLong() }

        val steps = generateSequence(initialPairCounts) { counts ->
            buildMap {
                counts.forEach { (pair, count) ->
                    val replacements = rules[pair]

                    if (replacements != null) {
                        val (one, other) = replacements
                        put(one, getOrDefault(one, 0) + count)
                        put(other, getOrDefault(other, 0) + count)
                    } else {
                        put(pair, count)
                    }
                }
            }
        }

        val finalPairCounts = steps
            .drop(n)
            .first()

        val (min, max) = finalPairCounts
            .asSequence()
            .map { (pair, count) -> pair[1] to count }
            .plus(initialPolymer[0] to 1L)
            .groupingBy { (letter, _) -> letter }
            .fold(0L) { acc, (_, count) -> acc + count }
            .values
            .minMaxLong()

        return max - min
    }

    private fun parse(input: List<String>): Polymerization {
        val iterator = input.iterator()
        val polymer = iterator.next()

        // skip the blank line
        iterator.next()

        val rules = buildMap<String, Pair<String, String>> {
            for (line in iterator) {
                val (pair, element) = line.split(" -> ")
                val (start, end) = pair.toCharArray()
                put(pair, "$start$element" to "$element$end")
            }
        }

        return Polymerization(polymer, rules)
    }

    private data class Polymerization(
        val polymer: String,
        val rules: Map<String, Pair<String, String>>
    )
}
