package org.chalup.advent2021

object Day14 {
    fun task1(input: List<String>): Int = input
        .let(this::parse)
        .let { (initialPolymer, rules) ->
            generateSequence(initialPolymer) { polymer ->
                polymer
                    .zipWithNext { a, b ->
                        val insertedElement = rules["$a$b"] ?: ""

                        "$insertedElement$b"
                    }
                    .joinToString(
                        separator = "",
                        prefix = "${polymer.first()}"
                    )
            }
        }
        .drop(10)
        .first()
        .let { polymer ->
            val occurrences = polymer
                .groupingBy { it }
                .eachCount()

            val (min, max) = occurrences
                .values
                .let {
                    var min = Int.MAX_VALUE
                    var max = Int.MIN_VALUE

                    for (i in it) {
                        min = minOf(i, min)
                        max = maxOf(i, max)
                    }

                    min to max
                }

            max - min
        }

    private fun parse(input: List<String>): Polymerization {
        val iterator = input.iterator()
        val polymer = iterator.next()

        // skip the blank line
        iterator.next()

        val rules = buildMap<String, String> {
            for (line in iterator) {
                val (pair, element) = line.split(" -> ")
                put(pair, element)
            }
        }

        return Polymerization(polymer, rules)
    }

    private data class Polymerization(
        val polymer: String,
        val rules: Map<String, String>
    )
}
