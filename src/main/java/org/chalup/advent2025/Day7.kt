package org.chalup.advent2025

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.plus
import java.util.LinkedList

object Day7 {
    fun task1(input: List<String>): Int {
        operator fun List<String>.get(p: Point) = this.getOrNull(p.y)?.getOrNull(p.x)

        val startingPoint = Point(x = input.first().indexOf('S'), y = 0)

        val visitedSplitters = mutableSetOf<Point>()
        val queue = LinkedList<Point>().apply { add(startingPoint) }

        while (queue.isNotEmpty()) {
            var p = queue.removeFirst()

            if (p in visitedSplitters) continue

            while (true) {
                p += Direction.D.vector
                when (input[p]) {
                    null -> break
                    '^' -> {
                        if (visitedSplitters.add(p)) {
                            queue += p + Direction.L.vector
                            queue += p + Direction.R.vector
                        }
                        break
                    }
                }
            }
        }

        return visitedSplitters.size
    }

    fun task2(input: List<String>): Long {
        return input
            .drop(1)
            .fold(mapOf(input.first().indexOf('S') to 1L)) { timelinesByX, line ->
                timelinesByX
                    .flatMap { (x, i) ->
                        if (line[x] == '.') {
                            sequenceOf(x to i)
                        } else {
                            sequenceOf(
                                x - 1 to i,
                                x + 1 to i,
                            )
                        }
                    }
                    .groupingBy { (x, _) -> x }
                    .aggregate { _, acc, (_, count), _ -> (acc ?: 0) + count }
            }
            .values
            .sum()
    }
}