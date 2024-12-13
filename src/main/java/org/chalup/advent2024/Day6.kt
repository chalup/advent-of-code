package org.chalup.advent2024

import org.chalup.advent2018.cycleNext
import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.plus

object Day6 {
    fun task1(input: List<String>): Int {
        operator fun List<String>.get(p: Point) = getOrNull(p.y)?.getOrNull(p.x)

        return calculatePath(findGuard(input), map = input::get)
            .let { it as Path.LeadingOutside }
            .steps
            .asSequence()
            .distinctBy { (p, _) -> p }
            .count()
    }

    fun task2(input: List<String>): Int {
        operator fun List<String>.get(p: Point) = getOrNull(p.y)?.getOrNull(p.x)

        val guard = findGuard(input)

        val nonObstructedPath = calculatePath(guard, map = input::get)
            .let { it as Path.LeadingOutside }
            .steps

        return nonObstructedPath
            .asSequence()
            .map { (p, _) -> p }
            .distinct()
            .filter { input[it] == '.' }
            .map { obstacleLocation ->
                val path = calculatePath(
                    guard,
                    map = { p -> if (p == obstacleLocation) '#' else input[p] }
                )

                path to obstacleLocation
            }
            .filter { (path, _) -> path is Path.Cycle }
            .distinctBy { (_, obstacleLocation) -> obstacleLocation }
            .count()
    }

    private fun calculatePath(
        guard: Pair<Point, Direction>,
        map: (Point) -> Char?
    ) : Path {
        val path = LinkedHashSet<Pair<Point, Direction>>()
        var (p, dir) = guard
        while (true) {
            if (!path.add(p to dir)) return Path.Cycle

            when (map(p + dir.vector)) {
                null -> break
                '#' -> dir = dir.cycleNext()
                else -> p += dir.vector
            }
        }

        return Path.LeadingOutside(path.toList())
    }

    private sealed interface Path {
        data class LeadingOutside(val steps: List<Pair<Point, Direction>>) : Path
        data object Cycle : Path
    }

    private fun findGuard(input: List<String>) = input
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

}