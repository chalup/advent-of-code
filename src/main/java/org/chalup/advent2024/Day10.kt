package org.chalup.advent2024

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.plus

object Day10 {
    fun task1(input: List<String>): Int {
        operator fun List<String>.get(p: Point) = this.getOrNull(p.y)?.getOrNull(p.x)?.digitToInt()

        val summitsByStartingPosition = mutableMapOf<Point, Set<Point>>()

        fun summits(p: Point): Set<Point> = summitsByStartingPosition.getOrPut(p) {
            val elevation = input[p]!!

            if (elevation == 9) {
                mutableSetOf(p)
            } else {
                Direction
                    .entries
                    .asSequence()
                    .map { dir -> p + dir.vector }
                    .filter { input[it] == elevation + 1 }
                    .flatMapTo(mutableSetOf()) { summits(it) }
            }
        }

        return input
            .flatMapIndexed { y, line ->
                line.mapIndexedNotNull { x, char ->
                    if (char == '0') Point(x, y) else null
                }
            }
            .sumOf { summits(it).size }
    }
}