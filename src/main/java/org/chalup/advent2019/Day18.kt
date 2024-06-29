package org.chalup.advent2019

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.plus
import java.util.LinkedList
import java.util.PriorityQueue
import kotlin.math.absoluteValue

object Day18 {
    fun task1(map: List<String>): Int {
        val keys = findKeys(map)
        val allKeys = keys.keys.fold(0L, Long::or)

        val playerLocation = findPlayer(map)

        val pathsToKeys: Map<Long, Map<Long, Path>> = findPaths(
            pointsOfInterest = keys + (0L to playerLocation),
            map = { p -> map[p.y][p.x] }
        )

        data class State(val location: Long, val collectedKeys: Long)

        val queue = PriorityQueue<Pair<State, Int>>(compareBy { (_, stepsTravelled) -> stepsTravelled }).apply {
            add(State(location = 0L, collectedKeys = 0L) to 0)
        }

        val visited = mutableSetOf<State>()
        while (queue.isNotEmpty()) {
            val (state, stepsTravelled) = queue.poll()

            if (!visited.add(state)) continue

            if (state.collectedKeys == allKeys) return stepsTravelled

            pathsToKeys.getValue(state.location)
                .asSequence()
                .filter { (to, _) -> (to and state.collectedKeys) == 0L }
                .filter { (_, path) -> (path.doorsMask and state.collectedKeys) == path.doorsMask }
                .map { (to, path) ->
                    State(
                        location = to,
                        collectedKeys = state.collectedKeys or to
                    ) to stepsTravelled + path.steps
                }
                .forEach(queue::add)
        }

        throw IllegalStateException("Could not find the solution :(")
    }

    fun task2(map: List<String>): Int {
        val keys = findKeys(map)
        val allKeys = keys.keys.fold(0L, Long::or)

        val playerLocation = findPlayer(map)
        val robotVectors = listOf(-1, +1).flatMap { dy -> listOf(-1, +1).map { dx -> Vector(dx, dy) } }
        val robots = robotVectors.withIndex().associate { (i, v) -> ((1L + i) shl 32) to (playerLocation + v) }

        val pathsToKeys: Map<Long, Map<Long, Path>> = findPaths(
            pointsOfInterest = keys + robots,
            map = { p ->
                val v = Vector(playerLocation, p)

                when {
                    v in robotVectors -> '@'
                    v.dx.absoluteValue <= 1 && v.dy.absoluteValue <= 1 -> '#'
                    else -> map[p.y][p.x]
                }
            }
        )

        data class State(val robotsLocations: List<Long>, val collectedKeys: Long)

        val queue = PriorityQueue<Pair<State, Int>>(compareBy { (_, stepsTravelled) -> stepsTravelled }).apply {
            add(
                State(robotsLocations = robots.keys.toList(), collectedKeys = 0L) to 0
            )
        }

        val visited = mutableSetOf<State>()
        while (queue.isNotEmpty()) {
            val (state, stepsTravelled) = queue.poll()

            if (!visited.add(state)) continue

            if (state.collectedKeys == allKeys) return stepsTravelled

            state.robotsLocations
                .withIndex()
                .asSequence()
                .flatMap { (robotIndex, robotLocation) ->
                    pathsToKeys[robotLocation]
                        .orEmpty()
                        .filter { (to, _) -> (to and state.collectedKeys) == 0L }
                        .filter { (_, path) -> (path.doorsMask and state.collectedKeys) == path.doorsMask }
                        .map { (to, path) ->
                            State(
                                robotsLocations = state.robotsLocations.toMutableList().apply { set(robotIndex, to) },
                                collectedKeys = state.collectedKeys or to
                            ) to stepsTravelled + path.steps
                        }
                }
                .forEach(queue::add)
        }

        throw IllegalStateException("Could not find the solution :(")
    }

    private fun findPaths(
        pointsOfInterest: Map<Long, Point>,
        map: (Point) -> Char
    ): Map<Long, Map<Long, Path>> = pointsOfInterest.mapValues { (from, location) ->
        buildMap {
            val visited = mutableSetOf<Point>()
            val queue = LinkedList<Pair<Point, Path>>().apply {
                add(location to Path(steps = 0, doorsMask = 0L))
            }

            while (queue.isNotEmpty()) {
                val (p, path) = queue.poll()

                if (!visited.add(p)) continue

                val tile = map(p)
                if (tile.isLowerCase()) {
                    val keyMask = tile.keyMask()
                    if (keyMask != from) {
                        put(keyMask, path)
                    }
                }

                val doorMask = if (tile.isUpperCase()) {
                    path.doorsMask or tile.lowercaseChar().keyMask()
                } else {
                    path.doorsMask
                }

                Direction.entries
                    .mapNotNullTo(queue) { d ->
                        val nextPosition = p + d.vector

                        (nextPosition to Path(steps = path.steps + 1, doorMask)).takeIf { map(nextPosition) != '#' }
                    }
            }
        }
    }

    private fun findPlayer(map: List<String>) = map
        .flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                if (c == '@') Point(x, y) else null
            }
        }
        .single()

    private fun findKeys(map: List<String>): Map<Long, Point> = buildMap {
        map.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                if (c.isLowerCase()) put(c.keyMask(), Point(x, y))
            }
        }
    }

    private fun Char.keyMask() = 1L shl (this - 'a' + 1)
    private data class Path(val steps: Int, val doorsMask: Long)
}