package org.chalup.advent2023

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.contains
import org.chalup.utils.manhattanDistance
import org.chalup.utils.plus
import org.chalup.utils.points
import java.util.LinkedList

object Day21 {
    fun task1(map: List<String>): Int {
        operator fun List<String>.get(p: Point) = this[p.y][p.x]

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
}
