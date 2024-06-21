package org.chalup.advent2016

import org.chalup.advent2015.Day4.md5
import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.contains
import org.chalup.utils.plus
import java.util.PriorityQueue

object Day17 {
    fun task1(input: List<String>): String {
        val maze = Maze(passcode = input.first())

        val visited = mutableSetOf<Pair<Point, String>>()
        val queue = PriorityQueue<Pair<Point, String>>(compareBy { (_, path) -> path.length }).apply {
            add(maze.bounds.topLeft to "")
        }

        while (queue.isNotEmpty()) {
            val state = queue.poll()
            if (!visited.add(state)) continue

            val (p, path) = state
            if (p == maze.bounds.bottomRight) return path

            maze.availableDirections(path)
                .mapNotNullTo(queue) { d ->
                    (p + d.vector)
                        .takeIf { it in maze.bounds }
                        ?.let { it to (path + d.symbol) }
                }
        }

        throw IllegalStateException("Could not find the solution :(")
    }

    fun task2(input: List<String>): Int {
        val maze = Maze(passcode = input.first())

        val visited = mutableSetOf<Pair<Point, String>>()
        val queue = mutableListOf(maze.bounds.topLeft to "")

        var longestSolution = 0

        while (queue.isNotEmpty()) {
            val state = queue.removeFirst()
            if (!visited.add(state)) continue

            val (p, path) = state
            if (p == maze.bounds.bottomRight) {
                longestSolution = maxOf(longestSolution, path.length)
            } else {
                maze.availableDirections(path)
                    .mapNotNullTo(queue) { d ->
                        (p + d.vector)
                            .takeIf { it in maze.bounds }
                            ?.let { it to (path + d.symbol) }
                    }
            }
        }

        return longestSolution
    }

    private class Maze(private val passcode: String) {
        val bounds = Rect(0, 0, 3, 3)

        fun availableDirections(path: String): List<Direction> {
            val doorOpeningRange = 'b'..'f'
            val hash = "$passcode$path".md5()

            return listOf(Direction.U, Direction.D, Direction.L, Direction.R).filterIndexed { index, _ ->
                hash[index] in doorOpeningRange
            }
        }
    }
}
