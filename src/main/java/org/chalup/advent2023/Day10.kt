package org.chalup.advent2023

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.contains
import org.chalup.utils.plus
import java.util.LinkedList

object Day10 {
    fun task1(map: List<String>): Int =
        resolveLoop(map).let { (it.count() + 1) / 2 }

    fun task2(map: List<String>): Int {
        // map the path onto 2x larger grid to handle squeezing between the pipes
        // flood fill from the outside
        // keep only tiles matching the smaller grid
        // inside = total - outside - loop pipes

        val path = resolveLoop(map).toList()

        val pathSegments = buildSet {
            path.forEach { (position, direction) ->
                val spacePosition = Point(
                    position.x * 2 + 1,
                    position.y * 2 + 1,
                )

                add(spacePosition)
                add(spacePosition + direction.vector)
            }
        }

        val spaceBounds = Rect(
            Point(0, 0),
            Point(
                map.first().length * 2,
                map.size * 2
            )
        )

        val outside = mutableSetOf<Point>()
        val heads = LinkedList<Point>().apply { add(Point(0, 0)) }

        while (heads.isNotEmpty()) {
            val head = heads.poll()

            if (outside.add(head)) {
                Direction
                    .values()
                    .mapNotNullTo(heads) { direction ->
                        (head + direction.vector)
                            .takeIf { it in spaceBounds }
                            ?.takeUnless { it in pathSegments }
                    }
            }
        }

        val allTilesCount = map.first().length * map.size
        val outsideTiles = outside.count { it.x % 2 != 0 && it.y % 2 != 0 }

        return allTilesCount - outsideTiles - path.size
    }

    private fun resolveLoop(map: List<String>): Sequence<Probe> {
        operator fun List<String>.get(point: Point): Char? = map
            .getOrNull(point.y)
            ?.getOrNull(point.x)

        val startingPosition = map
            .asSequence()
            .flatMapIndexed { y, line ->
                line
                    .asSequence()
                    .mapIndexedNotNull { x, c ->
                        if (c == 'S') {
                            Point(x, y)
                        } else {
                            null
                        }
                    }
            }
            .single()

        val initialProbe = Direction
            .values()
            .first { direction -> traversePipe(direction, map[startingPosition + direction.vector]) != null }
            .let {
                Probe(
                    location = startingPosition,
                    direction = it
                )
            }

        return generateSequence(initialProbe) { probe ->
            val newLocation = probe.location + probe.direction.vector

            if (newLocation == startingPosition) {
                null
            } else {
                val nextPipeSegment = map[newLocation]
                val newDirection = traversePipe(probe.direction, nextPipeSegment)
                    ?: throw IllegalStateException("Probe $probe encountered unexpected pipe segment $nextPipeSegment")

                Probe(
                    location = newLocation,
                    direction = newDirection,
                )
            }
        }
    }

    private fun traversePipe(direction: Direction, pipe: Char?): Direction? = when {
        direction == Direction.U && pipe == '|' -> Direction.U
        direction == Direction.U && pipe == 'F' -> Direction.R
        direction == Direction.U && pipe == '7' -> Direction.L

        direction == Direction.D && pipe == '|' -> Direction.D
        direction == Direction.D && pipe == 'L' -> Direction.R
        direction == Direction.D && pipe == 'J' -> Direction.L

        direction == Direction.R && pipe == '-' -> Direction.R
        direction == Direction.R && pipe == '7' -> Direction.D
        direction == Direction.R && pipe == 'J' -> Direction.U

        direction == Direction.L && pipe == '-' -> Direction.L
        direction == Direction.L && pipe == 'F' -> Direction.D
        direction == Direction.L && pipe == 'L' -> Direction.U

        else -> null
    }
}

private data class Probe(
    val location: Point,
    val direction: Direction,
)