package org.chalup.advent2016

import java.util.LinkedList

object Day10 {
    private fun simulate(input: List<String>): Pair<Int?, MutableMap<Destination, List<Int>>> {
        val (inputValues, botLinks) = parseInput(input)

        val stateByDestination = mutableMapOf<Destination, List<Int>>()

        var comparator: Int? = null

        for (i in inputValues) {
            val queue = LinkedList<Pair<Destination, Int>>().apply { add(Destination.Bot(i.bot) to i.value) }

            while (queue.isNotEmpty()) {
                val (dst, value) = queue.poll()

                val newState = stateByDestination[dst].orEmpty() + value

                if (dst is Destination.Bot && newState.size == 2) {
                    stateByDestination.remove(dst)
                    val (low, high) = newState.sorted()

                    if (low == 17 && high == 61) comparator = dst.id

                    val (lowDst, highDst) = botLinks.getValue(dst.id)

                    queue.add(lowDst to low)
                    queue.add(highDst to high)
                } else {
                    stateByDestination[dst] = newState
                }
            }
        }

        return comparator to stateByDestination
    }

    fun task1(input: List<String>): Int = simulate(input).let { (bot, _) -> bot!! }
    fun task2(input: List<String>): Int = simulate(input).let { (_, finalState) ->
        val bins: Set<Destination> = (0..2).mapTo(mutableSetOf(), Destination::OutputBin)

        finalState
            .filterKeys { it in bins }
            .values
            .map { it.single() }
            .reduce(Int::times)
    }

    private data class BotLinks(
        val lowGoesTo: Destination,
        val highGoesTo: Destination,
    )

    private sealed class Destination {
        data class OutputBin(val id: Int) : Destination()
        data class Bot(val id: Int) : Destination()
    }

    private data class InputValue(
        val value: Int,
        val bot: Int,
    )

    private fun parseInput(input: List<String>): Pair<List<InputValue>, Map<Int, BotLinks>> {
        val inputValues = mutableListOf<InputValue>()
        val botLinks = mutableMapOf<Int, BotLinks>()

        for (line in input) {
            if (line.startsWith("value")) {
                val (value, bot) = line
                    .split("value", "goes to bot")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .map { it.toInt() }

                inputValues += InputValue(value, bot)
            } else if (line.startsWith("bot")) {
                val bot = line.substringAfter("bot").substringBefore("gives").trim().toInt()

                val lowSpec = line.substringAfter("low to").substringBefore("and").trim()
                val highSpec = line.substringAfter("high to").trim()

                fun parseDestination(spec: String): Destination {
                    val id = spec.substringAfter(' ').toInt()

                    return if (spec.startsWith("bot")) {
                        Destination.Bot(id)
                    } else if (spec.startsWith("output")) {
                        Destination.OutputBin(id)
                    } else {
                        throw IllegalArgumentException(spec)
                    }
                }

                botLinks[bot] = BotLinks(
                    lowGoesTo = parseDestination(lowSpec),
                    highGoesTo = parseDestination(highSpec)
                )
            }
        }

        return inputValues to botLinks
    }
}

