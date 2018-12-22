package org.chalup.advent2018

import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.plus
import java.util.LinkedList

object Day20 {
    fun part1(input: String): Int = traverse(input)
        .values
        .max()!!

    fun traverse(input: String): Map<Point, Int> {
        val distances = mutableMapOf(Point(0, 0) to 0)

        var activeHeads = mutableSetOf<Point>()
        val waitingHeads = LinkedList<MutableSet<Point>>()
        val parkedHeads = LinkedList<MutableSet<Point>>()

        fun traverse(dx: Int, dy: Int) {
            activeHeads = activeHeads.mapNotNullTo(mutableSetOf()) { from ->
                val to = from + Vector(dx, dy)
                val distance = distances.getValue(from) + 1

                val knownDistance = distances[to]

                if (knownDistance != null && knownDistance <= distance) {
                    null
                } else {
                    to.also { distances[to] = distance }
                }
            }
        }

        input.forEach {
            when (it) {
                '^' -> activeHeads = mutableSetOf(Point(0, 0))
                'N' -> traverse(0, -1)
                'S' -> traverse(0, +1)
                'W' -> traverse(-1, 0)
                'E' -> traverse(+1, 0)
                '(' -> {
                    parkedHeads.push(activeHeads.toMutableSet())
                    waitingHeads.push(mutableSetOf())
                }
                '|' -> {
                    waitingHeads.peek().addAll(activeHeads)
                    activeHeads = parkedHeads.peek().toMutableSet()
                }
                ')' -> {
                    activeHeads.addAll(waitingHeads.pop())
                    parkedHeads.pop()
                }
                '$' -> return distances
            }
        }

        throw IllegalStateException("Impossiburu!")
    }
}
