package org.chalup.advent2017

import org.chalup.utils.parseNumbers

object Day6 {
    fun task1(input: List<String>) = input
        .first()
        .let { parseNumbers(it) }
        .let(::simulateUntilLoop)
        .count() - 1

    fun task2(input: List<String>) = input
        .first()
        .let { parseNumbers(it) }
        .let(::simulateUntilLoop)
        .let { it.lastIndex - it.indexOf(it.last()) }

    private fun simulateUntilLoop(initialConfiguration: List<Int>): List<List<Int>> {
        return buildList {
            add(initialConfiguration)
            var currentConfig = initialConfiguration

            do {
                val cellsToRedistribute = currentConfig.max()
                val startingIndex = currentConfig.indexOf(cellsToRedistribute)

                val newConfig = currentConfig.toMutableList()
                newConfig[startingIndex] = 0
                repeat(cellsToRedistribute) { i ->
                    newConfig[(startingIndex + 1 + i) % newConfig.size]++
                }

                currentConfig = newConfig
                add(newConfig)
            } while (indexOf(newConfig) == lastIndex)
        }
    }
}