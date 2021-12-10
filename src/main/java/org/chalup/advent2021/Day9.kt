package org.chalup.advent2021

import org.chalup.advent2018.Day15.adjacentTiles
import org.chalup.utils.Point
import java.util.LinkedList

object Day9 {
    fun task1(input: List<String>): Int = input
        .let(this::findLowPoints)
        .sumOf { (input[it] - '0') + 1 }

    fun task2(input: List<String>): Int = input
        .let(this::findLowPoints)
        .map { lowPoint ->
            val basin = mutableSetOf<Point>()
            val queue = LinkedList<Point>().apply { add(lowPoint) }

            while (queue.isNotEmpty()) {
                val point = queue.poll()
                if (input[point] != '9') {
                    basin.add(point)

                    point
                        .adjacentTiles()
                        .filterNot { it in basin }
                        .filterNot { it in queue }
                        .forEach { queue.add(it) }
                }
            }

            basin.size
        }
        .sortedDescending()
        .take(3)
        .fold(1, Int::times)

    private fun findLowPoints(input: List<String>) = input.flatMapIndexed { row, line ->
        line.mapIndexedNotNull { col, char ->
            when {
                input[row + 1, col] <= char -> null
                input[row, col + 1] <= char -> null
                input[row - 1, col] <= char -> null
                input[row, col - 1] <= char -> null
                else -> Point(col, row)
            }
        }
    }

    private operator fun List<String>.get(row: Int, col: Int) = getOrNull(row)?.getOrNull(col) ?: '9'
    private operator fun List<String>.get(point: Point) = get(col = point.x, row = point.y)
}
