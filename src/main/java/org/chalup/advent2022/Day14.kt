package org.chalup.advent2022

import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.bounds
import org.chalup.utils.contains
import org.chalup.utils.plus
import org.chalup.utils.range

object Day14 {
    fun task1(input: List<String>) = input
        .let(::parse)
        .also { map -> simulateFallingSand(map, hasFloor = false) }
        .count { (_, tile) -> tile == Tile.SAND }

    fun task2(input: List<String>) = input
        .let(::parse)
        .also { map -> simulateFallingSand(map, hasFloor = true) }
        .count { (_, tile) -> tile == Tile.SAND }

    private fun parse(input: List<String>): MutableMap<Point, Tile> = input
        .asSequence()
        .flatMap { line ->
            line
                .splitToSequence(" -> ")
                .map { coordinates ->
                    coordinates
                        .splitToSequence(',')
                        .map { it.toInt() }
                        .iterator()
                        .let { Point(x = it.next(), y = it.next()) }
                }
                .zipWithNext()
                .flatMap { (from, to) ->
                    sequence {
                        for (x in range(from.x, to.x)) {
                            for (y in range(from.y, to.y)) {
                                yield(Point(x, y))
                            }
                        }
                    }
                }
        }
        .fold(mutableMapOf()) { map, point ->
            map.apply { put(point, Tile.ROCK) }
        }

    private fun simulateFallingSand(map: MutableMap<Point, Tile>, hasFloor: Boolean) {
        val source = Point(500, 0)
        map[source] = Tile.SOURCE

        val bounds = map.keys.bounds()
        val floorLevel = (bounds.bottomRight.y + 2).takeIf { hasFloor }

        val flow = mutableListOf(source)

        val vectors = listOf(
            Vector(0, 1),
            Vector(-1, 1),
            Vector(1, 1),
        )

        fun isBlocked(point: Point) = point.y == floorLevel || point in map

        while(flow.isNotEmpty()) {
            val head = flow.last()

            when (val nextHead = vectors.firstNotNullOfOrNull { vector -> (head + vector).takeUnless(::isBlocked) }) {
                null -> {
                    map[head] = Tile.SAND
                    flow.removeLast()
                }
                else -> {
                    if (floorLevel != null || nextHead in bounds) {
                        flow.add(nextHead)
                    } else {
                        break
                    }
                }
            }
        }
    }
}

private enum class Tile {
    ROCK, SOURCE, SAND
}