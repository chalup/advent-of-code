package org.chalup.advent2024

import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.Vector
import org.chalup.utils.contains
import org.chalup.utils.minus
import org.chalup.utils.plus
import org.chalup.utils.unaryMinus
import org.chalup.utils.uniquePairs

object Day8 {
    fun task1(input: List<String>): Int {
        val bounds = Rect(0, 0, input.first().lastIndex, input.lastIndex)

        val groups = parseGroups(input)

        val antiNodes = groups
            .values
            .asSequence()
            .flatMap { locations -> locations.uniquePairs() }
            .flatMap { (a, b) -> Vector(a, b).let { v -> sequenceOf(a - v, b + v) } }
            .filterTo(mutableSetOf()) { it in bounds }

        return antiNodes.size
    }

    fun task2(input: List<String>): Int {
        val bounds = Rect(0, 0, input.first().lastIndex, input.lastIndex)

        val groups = parseGroups(input)

        fun nodes(origin: Point, vector: Vector): Sequence<Point> = sequence {
            var p = origin
            while (p in bounds) {
                yield(p)
                p += vector
            }
        }

        return groups
            .values
            .asSequence()
            .flatMap { locations -> locations.uniquePairs() }
            .flatMap { (a, b) ->
                val v = Vector(a, b)
                nodes(a, -v) + nodes(b, v)
            }
            .distinct()
            .count()
    }

    private fun parseGroups(input: List<String>): Map<Char, List<Point>> {
        val antennas = buildList {
            input.forEachIndexed { y, line ->
                line.forEachIndexed { x, c ->
                    if (c != '.') {
                        add(c to Point(x, y))
                    }
                }
            }
        }

        val groups = antennas.groupBy(
            keySelector = { (frequency, _) -> frequency },
            valueTransform = { (_, location) -> location },
        )

        return groups
    }
}
