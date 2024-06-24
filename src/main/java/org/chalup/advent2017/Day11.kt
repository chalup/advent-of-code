package org.chalup.advent2017

import kotlin.math.absoluteValue

object Day11 {
    fun task1(input: List<String>): Int = visitedLocations(input.single().split(","))
        .last()
        .let { (ns, ew) -> distanceFromOrigin(ns, ew) }

    fun task2(input: List<String>): Int = visitedLocations(input.single().split(","))
        .maxOf { (ns, ew) -> distanceFromOrigin(ns, ew) }

    private fun visitedLocations(steps: List<String>) = steps
        .runningFold(0 to 0) { (ns, ew), direction ->
            when (direction) {
                "n" -> (ns + 2) to ew
                "s" -> (ns - 2) to ew
                "ne" -> (ns + 1) to (ew + 1)
                "se" -> (ns - 1) to (ew + 1)
                "nw" -> (ns + 1) to (ew - 1)
                "sw" -> (ns - 1) to (ew - 1)
                else -> throw IllegalArgumentException("Unexpected direction $direction")
            }
        }

    private fun distanceFromOrigin(ns: Int, ew: Int) = maxOf((ns.absoluteValue + 1) / 2, ew.absoluteValue)
}