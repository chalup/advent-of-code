package org.chalup.advent2023

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.contains
import org.chalup.utils.plus
import java.util.LinkedList
import java.util.PriorityQueue

object Day23 {
    fun task1(map: List<String>): Int = findLongestPath(map, slipperySlopes = true)
    fun task2(map: List<String>): Int = findLongestPath(map, slipperySlopes = false)

    private fun findLongestPath(map: List<String>, slipperySlopes: Boolean): Int {
        operator fun List<String>.get(p: Point) = this[p.y][p.x]
        val bounds = Rect(0, 0, map.first().lastIndex, map.lastIndex)

        val startLocation = map.first().indexOf('.').let { Point(it, 0) }
        val finishLocation = map.last().indexOf('.').let { Point(it, map.lastIndex) }

        val segments = buildSet<PathSegment> {
            fun traversePathSegment(startingPoint: PathSegmentStart): PathSegment? {
                val path = mutableSetOf(startingPoint.location)

                var currentLocation = startingPoint.location
                var nextStep: Direction? = startingPoint.direction
                while (nextStep != null) {
                    currentLocation += nextStep.vector
                    path += currentLocation

                    if (currentLocation == finishLocation) break

                    when (map[currentLocation]) {
                        '>' -> if (nextStep != Direction.R) return null
                        '<' -> if (nextStep != Direction.L) return null
                        'v' -> if (nextStep != Direction.D) return null
                        '^' -> if (nextStep != Direction.U) return null
                        '.' -> {
                            val availableDirections = Direction.values()
                                .filter {
                                    val p = (currentLocation + it.vector)

                                    p !in path && p in bounds && map[p] != '#'
                                }

                            when (availableDirections.size) {
                                0 -> return null
                                1 -> nextStep = availableDirections.single()
                                else -> break
                            }
                        }

                        '#' -> return null
                        else -> throw IllegalStateException("Traversing path $path, looking at $currentLocation")
                    }
                }

                return PathSegment(startingPoint, end = currentLocation, length = path.size - 1)
            }

            val queue = LinkedList<PathSegmentStart>().apply { add(PathSegmentStart(startLocation, Direction.D)) }
            while (queue.isNotEmpty()) {
                val startingPoint = queue.poll()

                if (any { it.start == startingPoint }) continue

                val segment = traversePathSegment(startingPoint)
                if (segment != null) {
                    add(segment)

                    if (segment.end != finishLocation) {
                        Direction.values().mapTo(queue) { PathSegmentStart(segment.end, it) }
                    }
                }
            }
        }

        var maxPathLength = 0
        val queue = PriorityQueue<Path>(compareByDescending { it.traversedSegments.size }).apply { add(Path(startLocation, emptySet(), segments)) }
        while (queue.isNotEmpty()) {
            val currentPath = queue.poll()

            val newPaths = buildList {
                currentPath
                    .availableSegments
                    .filter { it.start.location == currentPath.currentLocation }
                    .filter { it !in currentPath.traversedSegments }
                    .mapTo(this) { segment ->
                        Path(
                            currentLocation = segment.end,
                            traversedSegments = currentPath.traversedSegments + segment,
                            availableSegments = currentPath.availableSegments.filterNotTo(mutableSetOf()) { it.end == currentPath.currentLocation || it.start.location == currentPath.currentLocation }
                        )
                    }

                if (!slipperySlopes) {
                    currentPath
                        .availableSegments
                        .filter { it.end == currentPath.currentLocation }
                        .filter { it !in currentPath.traversedSegments }
                        .mapTo(this) { segment ->
                            Path(
                                currentLocation = segment.start.location,
                                traversedSegments = currentPath.traversedSegments + segment,
                                availableSegments = currentPath.availableSegments.filterNotTo(mutableSetOf()) { it.end == currentPath.currentLocation || it.start.location == currentPath.currentLocation }
                            )
                        }
                }
            }

            newPaths
                .asSequence()
                .filter { it.potential > maxPathLength }
                .forEach { path ->
                    if (path.currentLocation == finishLocation) {
                        maxPathLength = maxOf(
                            path.traversedSegments.sumOf(PathSegment::length),
                            maxPathLength
                        )
                    } else {
                        queue.add(path)
                    }
                }
        }

        return maxPathLength
    }
}

private data class Path(
    val currentLocation: Point,
    val traversedSegments: Set<PathSegment>,
    val availableSegments: Set<PathSegment>,
) {
    val potential = (traversedSegments + availableSegments).sumOf { it.length }
}

private data class PathSegmentStart(val location: Point, val direction: Direction)
private data class PathSegment(val start: PathSegmentStart, val end: Point, val length: Int)