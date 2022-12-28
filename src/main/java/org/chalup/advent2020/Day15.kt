package org.chalup.advent2020

object Day15 {
    fun task1(input: List<String>) = input
        .let(::parseStartingNumbers)
        .let { simulateMemoryGame(it, finalTurn = 2020) }

    fun task2(input: List<String>) = input
        .let(::parseStartingNumbers)
        .let { simulateMemoryGame(it, finalTurn = 30_000_000) }

    private fun parseStartingNumbers(input: List<String>) = input
        .single()
        .split(",")
        .map(String::toInt)

    private fun simulateMemoryGame(startingNumbers: List<Int>, finalTurn: Int): Int {
        val indicesByNumber = mutableMapOf<Int, Pair<Int, Int?>>().apply {
            startingNumbers.forEachIndexed { index, i -> this[i] = index to null }
        }

        var lastNumberSpoken = startingNumbers.last()
        for (turnIndex in startingNumbers.size until finalTurn) {
            val (last, previous) = indicesByNumber.getValue(lastNumberSpoken)

            val numberToSpeak = when (previous) {
                null -> 0
                else -> last - previous
            }

            indicesByNumber[numberToSpeak] = indicesByNumber[numberToSpeak]
                ?.let { (last, _) -> turnIndex to last }
                ?: (turnIndex to null)

            lastNumberSpoken = numberToSpeak
        }

        return lastNumberSpoken
    }
}