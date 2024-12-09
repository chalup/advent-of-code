package org.chalup.advent2024

import org.chalup.advent2018.cycleNext
import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.plus

object Day6 {
    fun task1(input: List<String>): Int {
        operator fun List<String>.get(p: Point) = getOrNull(p.y)?.getOrNull(p.x)

        var (p, dir) = input
            .mapIndexedNotNull { y, line ->
                line
                    .mapIndexedNotNull { x, c ->
                        val direction = when (c) {
                            '>' -> Direction.R
                            '^' -> Direction.U
                            '<' -> Direction.L
                            'v' -> Direction.D
                            else -> null
                        }

                        direction?.let { Point(x, y) to it }
                    }
                    .singleOrNull()
            }
            .single()

        val visitedPoints = mutableSetOf<Point>()
        while (true) {
            visitedPoints += p

            when (input[p + dir.vector]) {
                null -> break
                '.' -> p += dir.vector
                else -> dir = dir.cycleNext()
            }
        }

        return visitedPoints.size
    }
}