package org.chalup.advent2023

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.contains
import org.chalup.utils.plus
import java.util.LinkedList

object Day16 {
    fun task1(map: List<String>): Int = countEnergizedTiles(map, initialBeam = Beam(Point(0, 0), Direction.R))
    fun task2(map: List<String>): Int {
        val initialBeams = buildList {
            map.first().indices.forEach { x ->
                add(Beam(Point(x, 0), Direction.D))
                add(Beam(Point(x, map.lastIndex), Direction.U))
            }

            map.indices.forEach { y ->
                add(Beam(Point(0, y), Direction.R))
                add(Beam(Point(map.first().lastIndex, y), Direction.L))
            }
        }

        return initialBeams.maxOf { countEnergizedTiles(map, it) }
    }

    private fun countEnergizedTiles(map: List<String>, initialBeam: Beam): Int {
        val bounds = Rect(0, 0, map.first().lastIndex, map.lastIndex)

        val seen = mutableSetOf<Beam>()
        val beams = LinkedList<Beam>().apply { add(initialBeam) }

        while (beams.isNotEmpty()) {
            val beam = beams.poll()

            if (!seen.add(beam)) continue

            val directions = buildList {
                when (val char = map[beam.location]) {
                    '.' -> add(beam.direction)
                    '-' -> when (beam.direction) {
                        Direction.L, Direction.R -> add(beam.direction)
                        Direction.U, Direction.D -> {
                            add(Direction.L)
                            add(Direction.R)
                        }
                    }

                    '|' -> when (beam.direction) {
                        Direction.U, Direction.D -> add(beam.direction)
                        Direction.L, Direction.R -> {
                            add(Direction.U)
                            add(Direction.D)
                        }
                    }

                    '\\' -> {
                        val direction = when (beam.direction) {
                            Direction.U -> Direction.L
                            Direction.R -> Direction.D
                            Direction.D -> Direction.R
                            Direction.L -> Direction.U
                        }

                        add(direction)
                    }

                    '/' -> {
                        val direction = when (beam.direction) {
                            Direction.U -> Direction.R
                            Direction.R -> Direction.U
                            Direction.D -> Direction.L
                            Direction.L -> Direction.D
                        }

                        add(direction)
                    }

                    else -> throw IllegalStateException("Unexpected char $char")
                }
            }

            directions.mapNotNullTo(beams) { direction ->
                Beam(beam.location + direction.vector, direction)
                    .takeIf { it.location in bounds }
            }
        }

        return seen
            .mapTo(mutableSetOf()) { it.location }
            .count()
    }

    private operator fun List<String>.get(point: Point) = this[point.y][point.x]

    private data class Beam(
        val location: Point,
        val direction: Direction
    )
}
