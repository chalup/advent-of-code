package org.chalup.advent2023

import org.chalup.utils.Point
import org.chalup.utils.manhattanDistance

object Day11 {
    fun task1(map: List<String>) = sumOfAllDistances(map, universeExpansionFactor = 2)
    fun task2(map: List<String>) = sumOfAllDistances(map, universeExpansionFactor = 1_000_000)

    private fun sumOfAllDistances(map: List<String>, universeExpansionFactor: Int): Long {
        fun List<Boolean>.toExpansionOffsetsMap() = this
            .runningFold(0) { sum, expand -> sum + if (expand) 1 else 0 }
            .withIndex()
            .associate { (index, value) -> index to value }

        val columnExpansionMap = map.first()
            .indices
            .map { x -> map.all { it[x] == '.' } }
            .toExpansionOffsetsMap()

        val rowExpansionMap = map
            .indices
            .map { y -> map[y].all { it == '.' } }
            .toExpansionOffsetsMap()

        val galaxies = buildList {
            map.forEachIndexed { y, line ->
                line.forEachIndexed { x, c ->
                    if (c == '#') {
                        add(
                            Point(
                                x + columnExpansionMap.getValue(x) * (universeExpansionFactor - 1),
                                y + rowExpansionMap.getValue(y) * (universeExpansionFactor - 1),
                            )
                        )
                    }
                }
            }
        }

        return galaxies
            .asSequence()
            .flatMapIndexed { i, galaxy ->
                galaxies
                    .asSequence()
                    .drop(i + 1)
                    .map { otherGalaxy ->
                        galaxy to otherGalaxy
                    }
            }
            .sumOf { (a, b) -> manhattanDistance(a, b).toLong() }
    }
}