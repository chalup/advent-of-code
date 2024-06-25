package org.chalup.advent2017

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.plus
import java.util.LinkedList

object Day14 {
    fun task1(input: List<String>): Int {
        val salt = input.single()

        return (0..127).sumOf { rowIndex ->
            Day10.knotHash("$salt-$rowIndex").sumOf { it.digitToInt(radix = 16).countOneBits() }
        }
    }

    fun task2(input: List<String>): Int {
        val salt = input.single()

        val usedSquares = (0..127).flatMapTo(mutableSetOf()) { y ->
            buildString {
                Day10.knotHash("$salt-$y").forEach {
                    append(it.digitToInt(radix = 16).toString(radix = 2).padStart(length = 4, padChar = '0'))
                }
            }.mapIndexedNotNull { x, c -> if (c == '1') Point(x, y) else null }
        }

        val sectors = mutableListOf<Set<Point>>()
        while (usedSquares.isNotEmpty()) {
            val visited = mutableSetOf<Point>()
            val queue = LinkedList<Point>().apply { add(usedSquares.first()) }

            while (queue.isNotEmpty()) {
                val square = queue.poll()
                if (!visited.add(square)) continue

                Direction.entries.mapNotNullTo(queue) { d -> (square + d.vector).takeIf { it in usedSquares } }
            }

            sectors += visited
            usedSquares -= visited
        }

        return sectors.size
    }
}