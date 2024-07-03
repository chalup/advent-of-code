package org.chalup.advent2018

import kotlin.math.absoluteValue

private typealias Star = List<Long>

object Day25 {
    fun task1(input: List<String>): Int {
        val stars: List<Star> = input.map { line ->
            line
                .split(",")
                .map(String::toLong)
        }

        val constellations = stars.fold(emptyList<List<Star>>()) { constellations, star ->
            val indicesOfMatchingConstellations = constellations.mapIndexedNotNull { index, constellationStars ->
                index.takeIf {
                    constellationStars.any { manhattanDistance(it, star) <= 3 }
                }
            }

            val (constellationsToMerge, otherConstellations) = constellations
                .withIndex()
                .partition { (index, _) -> index in indicesOfMatchingConstellations }

            val mergedConstellation: List<Star> = constellationsToMerge
                .flatMapTo(mutableListOf(star), IndexedValue<List<Star>>::value)

            otherConstellations
                .mapTo(mutableListOf(mergedConstellation), IndexedValue<List<Star>>::value)
        }

        return constellations.size
    }

    private fun manhattanDistance(one: Star, other: Star) = one.zip(other) { a, b -> (a - b).absoluteValue }.sum()
}