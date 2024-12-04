package org.chalup.advent2024

import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.plus
import org.chalup.utils.times

object Day4 {
    fun task1(input: List<String>): Int = sequence {
        for (y in input.indices) {
            for (x in input.first().indices) {
                val origin = Point(x, y)
                for (dx in -1..1) {
                    for (dy in -1..1) {
                        if (dx == 0 && dy == 0) continue
                        val direction = Vector(dx, dy)

                        yield(
                            List(4) { origin + direction * it }
                                .map { (x, y) -> input.getOrNull(y)?.getOrNull(x) }
                                .joinToString(separator = "")
                        )
                    }
                }

            }
        }
    }.count { it == "XMAS" }

    fun task2(input: List<String>): Int = sequence {
        operator fun List<String>.get(p: Point) = getOrNull(p.y)?.getOrNull(p.x)

        val ms = setOf('M', 'S')

        val diagonals = listOf(
            listOf(Vector(-1, -1), Vector(+1, +1)),
            listOf(Vector(-1, +1), Vector(+1, -1)),
        )

        for (y in input.indices) {
            for (x in input.first().indices) {
                val origin = Point(x, y)

                if (input[origin] == 'A') {
                    if (diagonals.all { deltas -> deltas.mapTo(mutableSetOf()) { input[origin + it] } == ms}) {
                        yield(origin)
                    }
                }
            }
        }
    }.count()
}