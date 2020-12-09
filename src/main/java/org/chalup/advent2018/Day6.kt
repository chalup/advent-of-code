package org.chalup.advent2018

import org.chalup.utils.Point
import org.chalup.utils.bounds
import org.chalup.utils.isOnTheEdgeOf
import org.chalup.utils.manhattanDistance
import org.chalup.utils.points

object Day6 {
    private class Area(val origin: Point)

    private fun parse(input: String): Area =
        input.split(",").map { it.trim().toInt() }.let { (x, y) -> Area(Point(x, y)) }

    fun findLargestFiniteArea(input: List<String>): Int = input
        .map { parse(it) }
        .let { areas ->
            val bounds = areas.map { it.origin }.bounds()

            bounds
                .points()
                .groupBy { point ->
                    areas
                        .groupBy { area -> manhattanDistance(area.origin, point) }
                        .minByOrNull { (distance, _) -> distance }!!
                        .let { (_, areas) -> areas.singleOrNull() }
                }
                .filterKeys { it != null }
                .filter { (_, points) -> points.none { it isOnTheEdgeOf bounds } }
                .map { (_, points) -> points.size }
                .maxOrNull()!!
        }

    fun findSafeRegionArea(input: List<String>, maxDistanceSum: Int): Int = input
        .map { parse(it) }
        .let { areas ->
            areas
                .map { it.origin }
                .bounds()
                .points()
                .asSequence()
                .map { point -> areas.map { area -> manhattanDistance(area.origin, point) }.sum() }
                .count { it < maxDistanceSum }
        }
}