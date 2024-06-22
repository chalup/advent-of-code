package org.chalup.advent2015

import org.chalup.utils.permutations

object Day13 {
    fun task1(input: List<String>): Int = calculateTableHappiness(input)
    fun task2(input: List<String>): Int = calculateTableHappiness(input, additionalGuests = setOf("Me, Myself, and I"))


    private fun calculateTableHappiness(
        input: List<String>,
        additionalGuests: Set<String> = emptySet()
    ): Int {
        val rules: Map<String, Map<String, Int>> = input
            .map { line ->
                val segments = line.split(" ")
                val personA = segments[0]
                val personB = segments.last().trim('.')
                val multiplier = when (val verb = segments[2]) {
                    "gain" -> +1
                    "lose" -> -1
                    else -> throw IllegalArgumentException(verb)
                }
                val units = segments[3].toInt()

                Triple(personA, personB, units * multiplier)
            }
            .groupBy { (who, _, _) -> who }
            .mapValues { (_, rules) ->
                rules
                    .associate { (_, otherPerson, change) -> otherPerson to change }
                    .withDefault { 0 }
            }
            .withDefault { mapOf<String, Int>().withDefault { 0 } }

        return permutations(rules.keys + additionalGuests)
            .maxOf { arrangement ->
                val pairs = (arrangement.zipWithNext() + (arrangement.first() to arrangement.last())).toSet()

                pairs.sumOf { (a, b) -> rules.getValue(a).getValue(b) + rules.getValue(b).getValue(a) }
            }
    }
}