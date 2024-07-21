package org.chalup.advent2019

import org.chalup.utils.Direction
import org.chalup.utils.Point

object Day17 {
    fun task1(input: List<String>): Int {
        val output = IntcodeInterpreter.runProgram(input.single())

        val map = output
            .joinToString(separator = "") { Char(it.toInt()).toString() }
            .split("\n")

        val intersections = buildSet {
            map.forEachIndexed { y, line ->
                line.forEachIndexed { x, char ->
                    if (char == '#' && Direction.entries.all { map.getOrNull(y + it.vector.dy)?.getOrNull(x + it.vector.dx) == '#' })
                        add(Point(x, y))
                }
            }
        }

        return intersections.sumOf { (x, y) -> x * y }
    }
}