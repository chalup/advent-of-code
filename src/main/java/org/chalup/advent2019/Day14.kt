package org.chalup.advent2019

import org.chalup.advent2019.Day14.Chemicals

object Day14 {
    data class Chemicals(val symbol: String, val quantity: Long)
    data class Reaction(val inputs: List<Chemicals>, val output: Chemicals)

    private fun parseInput(input: List<String>): List<Reaction> = input.map { parseReaction(it) }
    private fun parseReaction(input: String): Reaction = input.split("=>").let { (inputs, output) ->
        Reaction(inputs = inputs.split(",").map { parseChemicals(it) },
                 output = parseChemicals(output))
    }

    private fun parseChemicals(input: String): Chemicals = input.trim().split(" ").let { (quantity, symbol) ->
        Chemicals(symbol, quantity.toLong())
    }

    fun oreCostOfOneFuel(reactionsList: List<String>): Long {
        val reactions = parseInput(reactionsList)
        fun reactionFor(symbol: String) = reactions.single { it.output.symbol == symbol }

        val chemicalsComplexity = mutableMapOf("ORE" to 0)
        fun getComplexity(symbol: String): Int = chemicalsComplexity.getOrPut(symbol) {
            reactionFor(symbol).inputs.asSequence().map { getComplexity(it.symbol) }.max()!! + 1
        }

        val requirementsSequence = generateSequence(listOf(Chemicals("FUEL", 1))) { inputs ->
            val nextChemicals = inputs.asIterable().maxBy { (symbol, _) -> getComplexity(symbol) }!!

            if (nextChemicals.symbol == "ORE") return@generateSequence null

            val reaction = reactionFor(nextChemicals.symbol)
            val reactionCount = (nextChemicals.quantity / reaction.output.quantity) +
                    if (nextChemicals.quantity % reaction.output.quantity == 0L) 0 else 1

            (inputs - nextChemicals + (reaction.inputs * reactionCount))
                .groupBy { it.symbol }
                .map { (symbol, chemicals) -> Chemicals(symbol, chemicals.sumBy { it.quantity }) }
        }

        return requirementsSequence
            .last()
            .single()
            .also { check(it.symbol == "ORE") }
            .quantity
    }
}

private operator fun List<Chemicals>.times(n: Long) = map { (symbol, quantity) -> Chemicals(symbol, quantity * n) }

private inline fun <T> Iterable<T>.sumBy(selector: (T) -> Long): Long {
    var sum: Long = 0
    for (element in this) {
        sum += selector(element)
    }
    return sum
}
