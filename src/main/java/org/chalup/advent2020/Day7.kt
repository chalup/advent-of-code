package org.chalup.advent2020

import org.chalup.utils.match

object Day7 {
    fun task1(input: List<String>): Int = input
        .map { parseRule(it) }
        .let { rules ->
            val results = mutableSetOf<String>()

            val rulesToProcess = rules.toMutableSet()
            do {
                val matchingRules = rulesToProcess
                    .filter { rule -> rule.contents.keys.any { it == "shiny gold" || it in results } }

                rulesToProcess -= matchingRules
                results += matchingRules.map { it.bagColor }
            } while (matchingRules.isNotEmpty())

            results.size
        }

    fun task2(input: List<String>): Int = input
        .map { parseRule(it) }
        .let { rules ->
            val contentsLookup = rules.associate { it.bagColor to it.contents }

            fun numberOfBagsWithin(color: String): Int = contentsLookup
                .getValue(color)
                .entries
                .sumOf { (color, amount) -> numberOfBagsWithin(color) * amount + amount }

            numberOfBagsWithin("shiny gold")
        }

    private data class Rule(
        val bagColor: String,
        val contents: Map<String, Int>
    )

    private fun parseRule(text: String): Rule = match(text) {
        pattern("""(.*) bags contain (.*)""") { (bagColor, contentsText) ->
            val matcher = """(?:(?:(\d+) (.*?) bags?)(?:, |.))""".toPattern().matcher(contentsText)

            val contents = mutableMapOf<String, Int>()

            while (matcher.find()) {
                val amount = matcher.toMatchResult().group(1)
                val color = matcher.toMatchResult().group(2)

                contents[color] = amount.toInt()
            }

            Rule(bagColor, contents)
        }
    }
}