package org.chalup.advent2018

import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.plus
import java.util.LinkedList

object Day20 {
    data class TraverseHead(val location: Point = Point(0, 0),
                            val travelledDistance: Int = 0)

    fun part1(input: String): Int = traverse(input)
        .also { println("${it.size}") }
        .groupBy { it.location }
        .mapValues { (_, heads) -> heads.map { it.travelledDistance }.min()!! }
        .values
        .max()!!

    fun traverse(input: String): MutableSet<TraverseHead> {
        var activeHeads = mutableSetOf<TraverseHead>()
        val waitingHeads = LinkedList<MutableSet<TraverseHead>>()
        val parkedHeads = LinkedList<MutableSet<TraverseHead>>()

        fun traverse(dx: Int, dy: Int) {
            activeHeads = activeHeads.mapTo(mutableSetOf()) {
                TraverseHead(location = it.location + Vector(dx, dy),
                             travelledDistance = it.travelledDistance + 1)
            }
        }

        input.forEach {
            when (it) {
                '^' -> activeHeads = mutableSetOf(TraverseHead())
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
                '$' -> return activeHeads
            }
        }

        throw IllegalStateException("Impossiburu!")
    }
}
