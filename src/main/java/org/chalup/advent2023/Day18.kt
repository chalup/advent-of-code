package org.chalup.advent2023

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.plus
import org.chalup.utils.times
import kotlin.math.absoluteValue

object Day18 {
    fun task1(input: List<String>) = calculateLagoonSize(input) { instructionLine ->
        DiggerInstruction(
            direction = Direction.fromSymbol(instructionLine.substringBefore(' ').trim()),
            length = instructionLine.substringAfter(' ').substringBefore('(').trim().toInt(),
        )
    }

    fun task2(input: List<String>): Long = calculateLagoonSize(input) { instructionLine ->
        instructionLine
            .substringAfter('#')
            .substringBefore(')')
            .let {
                DiggerInstruction(
                    length = it.dropLast(1).toInt(radix = 16),
                    direction = when (it.last()) {
                        '0' -> Direction.R
                        '1' -> Direction.D
                        '2' -> Direction.L
                        '3' -> Direction.U
                        else -> throw IllegalArgumentException()
                    }
                )
            }
    }

    private fun calculateLagoonSize(input: List<String>, parseInstruction: (String) -> DiggerInstruction) = input
        .map(parseInstruction)
        .let { instructions ->
            val interior = instructions
                .runningFold(Point(0, 0)) { point, instruction -> point + (instruction.direction.vector * instruction.length) }
                .let(::enclosedArea)

            // imagine calculating the interior from the middle of each tile
            // there's additional half tile sticking outside for every tile on the perimeter
            // the concave and convex corners add up to half a tile pairwise
            // then we have additional quarter of the tile built from the unpaired four corners
            val perimeter = instructions.sumOf { it.length.toLong() } / 2 + 1

            interior + perimeter
        }

    private fun enclosedArea(points: List<Point>): Long {
        check(points.first() == points.last())

        return (0 until points.lastIndex)
            .sumOf {
                val a = points[it]
                val b = points[it + 1]

                ((a.y + b.y).toLong() * (a.x - b.x).toLong())
            }
            .div(2)
            .absoluteValue
    }

    private data class DiggerInstruction(
        val direction: Direction,
        val length: Int,
    )
}