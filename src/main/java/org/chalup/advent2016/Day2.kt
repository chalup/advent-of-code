package org.chalup.advent2016

import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.plus

object Day2 {
    fun task1(input: List<String>): String = traceKeypad(input, keypadMap = """
        123
        456
        789
    """.trimIndent())

    fun task2(input: List<String>): String = traceKeypad(input, keypadMap = """
        |  1
        | 234
        |56789
        | ABC
        |  D
    """.trimMargin())

    private fun traceKeypad(directions: List<String>, keypadMap: String): String {
        val keypadPoints = buildMap {
            keypadMap.lines().forEachIndexed { y, line ->
                line.forEachIndexed { x, char ->
                    if (char != ' ') put(Point(x, y), char)
                }
            }
        }

        val initialPosition = keypadPoints.entries.firstNotNullOf { (position, char) -> position.takeIf { char == '5' } }

        return directions
            .scan(initialPosition) { fingerPosition, fingerMovements ->
                fingerMovements
                    .map { symbol -> FingerMovement.values().first { it.symbol == symbol } }
                    .fold(fingerPosition) { position, step ->
                        (position + step.vector)
                            .takeIf { it in keypadPoints }
                            ?: position
                    }
            }
            .drop(1)
            .map(keypadPoints::getValue)
            .joinToString(separator = "")
    }

    private enum class FingerMovement(val symbol: Char, val vector: Vector) {
        UP('U', Vector(0, -1)),
        DOWN('D', Vector(0, 1)),
        LEFT('L', Vector(-1, 0)),
        RIGHT('R', Vector(1, 0)),
    }
}