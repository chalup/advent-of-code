package org.chalup.advent2016

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.plus
import java.util.PriorityQueue

object Day13 {
    fun task1(input: List<String>): Int {
        val maze = Maze(magicConstant = input.first().toInt())

        val goal = Point(31, 39)

        val visited = mutableSetOf<Point>()
        val queue = PriorityQueue<Pair<Point, Int>>(compareBy { (_, steps) -> steps }).apply { add(Point(1, 1) to 0) }

        while (queue.isNotEmpty()) {
            val (p, steps) = queue.poll()
            if (!visited.add(p)) continue

            if (p == goal) return steps

            Direction
                .entries
                .mapNotNullTo(queue) { d ->
                    (p + d.vector)
                        .takeIf(maze::isPassable)
                        ?.let { it to steps + 1 }
                }
        }

        throw IllegalStateException("Could not find the solution :(")
    }

    fun task2(input: List<String>): Int {
        val maze = Maze(magicConstant = input.first().toInt())

        val visited = mutableSetOf<Point>()
        val queue = PriorityQueue<Pair<Point, Int>>(compareBy { (_, steps) -> steps }).apply { add(Point(1, 1) to 0) }

        while (queue.isNotEmpty()) {
            val (p, steps) = queue.poll()
            if (!visited.add(p)) continue
            if (steps == 50) continue

            Direction
                .entries
                .mapNotNullTo(queue) { d ->
                    (p + d.vector)
                        .takeIf(maze::isPassable)
                        ?.let { it to steps + 1 }
                }
        }

        return visited.size
    }

    class Maze(private val magicConstant: Int) {
        val map = mutableMapOf<Point, Boolean>()

        fun isPassable(point: Point) = map.getOrPut(point) {
            val (x, y) = point

            when {
                x < 0 -> false
                y < 0 -> false
                else -> (x * x + 3 * x + 2 * x * y + y + y * y + magicConstant)
                    .countOneBits() % 2 == 0
            }
        }
    }
}
