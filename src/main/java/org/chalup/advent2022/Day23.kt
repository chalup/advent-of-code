package org.chalup.advent2022

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.bounds
import org.chalup.utils.plus

object Day23 {
    fun task1(input: List<String>) = input
        .let(::parseElvesLocations)
        .let(::simulate)
        .drop(10)
        .first()
        .let { elvesLocations ->
            elvesLocations.bounds()
                .let { it.width * it.height }
                .minus(elvesLocations.size)
        }
}

private fun simulate(initialState: Set<Point>): Sequence<Set<Point>> =
    generateSequence(0 to initialState) { (step, elves) ->
        (step + 1) to simulateStep(step, elves)
    }.map { (_, elves) -> elves }

private val preferredDirectionChange: List<Pair<Direction, List<Vector>>> = listOf(
    Direction.U to listOf(Vector(-1, -1), Vector(0, -1), Vector(0, -1)),
    Direction.D to listOf(Vector(-1, +1), Vector(0, +1), Vector(0, +1)),
    Direction.L to listOf(Vector(-1, -1), Vector(-1, 0), Vector(-1, +1)),
    Direction.R to listOf(Vector(+1, -1), Vector(+1, 0), Vector(+1, +1)),
)

private fun Point.surroundingPoints(): Sequence<Point> = sequence {
    for (dx in -1..1)
        for (dy in -1..1)
            if (dx != 0 || dy != 0)
                yield(Point(x + dx, y + dy))
}

private fun simulateStep(step: Int, elves: Set<Point>): Set<Point> {
    val proposedMoves = buildList {
        for (e in elves) {
            if (e.surroundingPoints().none { it in elves }) continue

            for (i in preferredDirectionChange.indices) {
                val (d, vectors) = preferredDirectionChange[(i + step) % preferredDirectionChange.size]

                if (vectors.none { (e + it) in elves }) {
                    add(e to (e + d.vector))
                    break
                }
            }
        }
    }

    val acceptedMoves = buildMap {
        for ((from, to) in proposedMoves) {
            if (containsKey(from)) {
                put(to, null)
            } else {
                put(to, from)
            }
        }
    }

    return buildSet {
        addAll(elves)
        for ((to, from) in acceptedMoves) {
            if (from != null) {
                remove(from)
                add(to)
            }
        }
    }.also { check(it.size == elves.size) }
}

private fun parseElvesLocations(input: List<String>): Set<Point> = input
    .flatMapIndexedTo(mutableSetOf()) { y, line ->
        line.mapIndexedNotNull { x, c ->
            if (c == '#') Point(x, y) else null
        }
    }