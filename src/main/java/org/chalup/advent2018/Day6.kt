package org.chalup.advent2018

import java.lang.Math.abs

object Day6 {
    private data class Point(val x: Int, val y: Int)
    private data class Bounds(val minX: Int,
                              val minY: Int,
                              val maxX: Int,
                              val maxY: Int)

    private class Area(val origin: Point)

    private fun manhattanDistance(a: Point, b: Point) = abs(a.x - b.x) + abs(a.y - b.y)

    private fun parse(input: String): Area =
        input.split(",").map { it.trim().toInt() }.let { (x, y) -> Area(Point(x, y)) }

    private operator fun <T, U> Iterable<T>.times(other: Iterable<U>) = flatMap { t -> other.map { u -> t to u } }

    private fun Iterable<Area>.bounds(): Bounds {
        val (minX, maxX) = map { it.origin.x }.let { it.min()!! to it.max()!! }
        val (minY, maxY) = map { it.origin.y }.let { it.min()!! to it.max()!! }

        return Bounds(minX, minY, maxX, maxY)
    }

    private fun Bounds.points() = ((minX..maxX) * (minY..maxY)).map { (x, y) -> Point(x, y) }

    private infix fun Point.isOnTheEdgeOf(bounds: Bounds) = with(bounds) {
        when {
            x == minX -> true
            x == maxX -> true
            y == minY -> true
            y == maxY -> true
            else -> false
        }
    }

    fun findLargestFiniteArea(input: List<String>): Int = input
        .map { parse(it) }
        .let { areas ->
            val bounds = areas.bounds()

            bounds
                .points()
                .groupBy { point ->
                    areas
                        .groupBy { area -> manhattanDistance(area.origin, point) }
                        .minBy { (distance, _) -> distance }!!
                        .let { (_, areas) -> areas.singleOrNull() }
                }
                .filterKeys { it != null }
                .filter { (_, points) -> points.none { it isOnTheEdgeOf bounds } }
                .map { (_, points) -> points.size }
                .max()!!
        }

    fun findSafeRegionArea(input: List<String>, maxDistanceSum: Int): Int = input
        .map { parse(it) }
        .let { areas ->
            areas
                .bounds()
                .points()
                .asSequence()
                .map { point -> areas.map { area -> manhattanDistance(area.origin, point) }.sum() }
                .count { it < maxDistanceSum }
        }
}