package org.chalup.advent2015

object Day16 {
    fun task1(input: List<String>): Int = determineAunt(input) { _, value, tickerTapeValue -> tickerTapeValue != value }
    fun task2(input: List<String>): Int = determineAunt(input) { property, value, tickerTapeValue ->
        when (property) {
            "cats", "trees" -> value <= tickerTapeValue
            "pomeranians", "goldfish" -> value >= tickerTapeValue
            else -> value != tickerTapeValue
        }
    }

    private fun determineAunt(
        input: List<String>,
        excludeByProperty: (property: String, value: Int, tickerTapeValue: Int) -> Boolean
    ): Int {
        val aunts = input.associate { line ->
            val number = line.substringBefore(':').substringAfter(' ').toInt()
            val properties = line.substringAfter(':')
                .trim()
                .split(", ")
                .associate { spec ->
                    val (property, value) = spec.split(": ")
                    property to value.toInt()
                }

            number to properties
        }

        val tickerTape = mapOf(
            "children" to 3,
            "cats" to 7,
            "samoyeds" to 2,
            "pomeranians" to 3,
            "akitas" to 0,
            "vizslas" to 0,
            "goldfish" to 5,
            "trees" to 3,
            "cars" to 2,
            "perfumes" to 1,
        )

        return aunts
            .filterNot { (_, properties) ->
                properties.any { (property, value) ->
                    excludeByProperty(property, value, tickerTape.getValue(property))
                }
            }
            .keys
            .single()
    }
}
