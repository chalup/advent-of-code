package org.chalup.advent2023

import org.chalup.advent2018.cycleNext
import org.chalup.advent2018.cyclePrev
import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.contains
import org.chalup.utils.plus
import java.util.PriorityQueue

object Day17 {
    fun task1(input: List<String>): Int {
        val bounds = Rect(0, 0, input.first().lastIndex, input.lastIndex)
        val seen = mutableSetOf<Head>()
        val heads = PriorityQueue<Pair<Head, Int>>(compareBy { (_, heatLoss) -> heatLoss }).apply {
            Direction.values().mapTo(this) {
                Head(
                    Point(0, 0),
                    direction = it,
                    stepsBeforeTurn = 3
                ) to 0
            }
        }

        while (heads.isNotEmpty()) {
            val (head, heatLoss) = heads.poll()

            if (!seen.add(head)) continue

            val newLocation = (head.location + head.direction.vector)
                .takeIf { it in bounds }
                ?: continue
            val updatedHeatLoss = heatLoss + input[newLocation].digitToInt()

            if (newLocation == bounds.bottomRight) return updatedHeatLoss

            if (head.stepsBeforeTurn > 1) {
                heads.add(
                    Head(
                        newLocation,
                        head.direction,
                        stepsBeforeTurn = head.stepsBeforeTurn - 1
                    ) to updatedHeatLoss
                )
            }

            for (d in sequenceOf(head.direction.cycleNext(), head.direction.cyclePrev())) {
                heads.add(
                    Head(
                        newLocation,
                        d,
                        stepsBeforeTurn = 3
                    ) to updatedHeatLoss
                )
            }
        }

        throw IllegalStateException("Couldn't find the route :(")
    }

    fun task2(input: List<String>): Int = TODO()

    private operator fun List<String>.get(point: Point) = this[point.y][point.x]

    private data class Head(
        val location: Point,
        val direction: Direction,
        val stepsBeforeTurn: Int,
    )
}