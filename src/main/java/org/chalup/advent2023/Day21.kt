package org.chalup.advent2023

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.contains
import org.chalup.utils.manhattanDistance
import org.chalup.utils.plus
import org.chalup.utils.points
import java.util.LinkedList
import java.util.PriorityQueue

object Day21 {
    private operator fun List<String>.get(p: Point) = this[p.y][p.x]

    fun task1(map: List<String>): Int {
        val bounds = Rect(0, 0, map.first().lastIndex, map.lastIndex)
        val elf = bounds.points().single { map[it] == 'S' }

        val seen = mutableSetOf<Point>()
        val queue = LinkedList<Pair<Point, Int>>().apply { add(elf to 0) }

        while (queue.isNotEmpty()) {
            val (position, stepsTaken) = queue.poll()

            if (!seen.add(position)) continue
            if (stepsTaken == 64) continue

            Direction.values()
                .mapNotNullTo(queue) { direction ->
                    (position + direction.vector)
                        .takeIf { it in bounds }
                        ?.takeUnless { map[it] == '#' }
                        ?.let { it to (stepsTaken + 1) }
                }
        }

        return seen.count { manhattanDistance(it, elf) % 2 == 0 }
    }

    fun task2(map: List<String>): Long {
        val bounds = Rect(0, 0, map.first().lastIndex, map.lastIndex)
        val elf = bounds.points().single { map[it] == 'S' }

        val costWithinBounds = buildMap {
            val queue = PriorityQueue<Pair<Point, Int>>(compareBy { (_, steps) -> steps }).apply { add(elf to 0) }
            while (queue.isNotEmpty()) {
                val (position, stepsTaken) = queue.poll()

                if (position in this) continue
                put(position, stepsTaken)

                Direction.values()
                    .mapNotNullTo(queue) { direction ->
                        (position + direction.vector)
                            .takeIf { it in bounds }
                            ?.takeUnless { map[it] == '#' }
                            ?.let { it to (stepsTaken + 1) }
                    }
            }
        }

        // Grid is 131x131 square, the NSEW paths are clear of obstacles,
        // the elf starts dead center in the middle and 26501365 % 131 is
        // conveniently 65.
        //
        // So the shape we're looking at is the diamond with some corners
        // cut. The number of steps is odd and the sector size is odd, so
        // the sectors will alternate between the ones where we're looking
        // for odd number of steps and even number of steps.
        //
        // At the edges of the shape we need to account for the corners, but
        // they can be arranged in groups of N/N+1 corners. Too lazy to draw
        // an ASCII art here, but grab the paper and the pencil and see for
        // yourself.

        val n = (26501365 / 131).toLong()

        val viablePositionsInOddSectors = costWithinBounds.count { (_, steps) -> steps % 2 == 1 }
        val viablePositionsInEvenSectors = costWithinBounds.count { (_, steps) -> steps % 2 == 0 }
        val viablePositionsInCornersOfOddSectors = costWithinBounds.count { (_, steps) -> steps > 65 && steps % 2 == 1 }
        val viablePositionsInCornersOfEvenSectors = costWithinBounds.count { (_, steps) -> steps > 65 && steps % 2 == 0 }

        return listOf(
            (n + 1) * (n + 1) * viablePositionsInOddSectors,
            n * n * viablePositionsInEvenSectors,
            n * viablePositionsInCornersOfEvenSectors,
            -(n + 1) * viablePositionsInCornersOfOddSectors
        ).sum()
    }
}
