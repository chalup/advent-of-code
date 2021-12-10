package org.chalup.advent2021

import org.chalup.utils.Point
import org.chalup.utils.match
import kotlin.math.abs

object Day5 {
    fun task1(input: List<String>): Int {
        val lines = parseInput(input)

        val affectedSquares = buildMap<Point, Int> {
            lines
                .asSequence()
                .flatMap { it.points() }
                .forEach { this[it] = this.getOrDefault(it, 0) + 1 }
        }

        return affectedSquares.count { (_, ventsCount) -> ventsCount > 1 }
    }

    fun task2(input: List<String>): Int {
        val lines = parseInput(input)

        val affectedSquares = buildMap<Point, Int> {
            lines
                .asSequence()
                .flatMap { it.points(includeDiagonals = true) }
                .forEach { this[it] = this.getOrDefault(it, 0) + 1 }
        }

        return affectedSquares.count { (_, ventsCount) -> ventsCount > 1 }

        // 17990 - too high
    }

    private fun parseInput(input: List<String>) = input.map { inputLine ->
        match<Line>(inputLine) {
            pattern("""(\d+),(\d+) -> (\d+),(\d+)""") { (x1, y1, x2, y2) ->
                Line(
                    x1.toInt(),
                    y1.toInt(),
                    x2.toInt(),
                    y2.toInt(),
                )
            }
        }
    }

    private data class Line(
        val x1: Int,
        val y1: Int,
        val x2: Int,
        val y2: Int,
    )

    private fun Line.points(includeDiagonals: Boolean = false): Sequence<Point> = when {
        x1 == x2 -> (minOf(y1, y2)..maxOf(y1, y2)).asSequence().map { Point(x1, it) }
        y1 == y2 -> (minOf(x1, x2)..maxOf(x1, x2)).asSequence().map { Point(it, y1) }
        includeDiagonals && abs(x1 - x2) == abs(y1 - y2) -> (x1 rangeTo x2).zip(y1 rangeTo y2, ::Point).asSequence()
        else -> emptySequence()
    }

    infix fun Int.rangeTo(other: Int): IntProgression = if (this < other) this..other else this downTo other
}

