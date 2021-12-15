package org.chalup.advent2021

import org.chalup.utils.Point
import org.chalup.utils.bounds
import java.util.PriorityQueue

object Day15 {
    fun task1(input: List<String>): Int = input
        .let(this::parse)
        .let(this::findBestPath)
        .cumulativeRisk

    fun task2(input: List<String>): Int = input
        .let(this::parse)
        .let(this::tileMap)
        .let(this::findBestPath)
        .cumulativeRisk

    private fun tileMap(riskMap: Map<Point, Int>): Map<Point, Int> {
        val bounds = riskMap.keys.bounds()

        return buildMap {
            for (dx in 0 until 5)
                for (dy in 0 until 5) {
                    for ((point, risk) in riskMap) {
                        put(
                            Point(
                                point.x + dx * bounds.width,
                                point.y + dy * bounds.height,
                            ),
                            (risk + dx + dy).let { if (it > 9) it - 9 else it }
                        )
                    }
                }
        }
    }

    private fun findBestPath(riskMap: Map<Point, Int>): Head {
        val bounds = riskMap.keys.bounds()
        val (start, destination) = bounds.let { it.topLeft to it.bottomRight }

        val traversedTiles = mutableSetOf<Point>()
        val queue = PriorityQueue(compareBy<Head> { it.cumulativeRisk })

        queue.add(Head(start, cumulativeRisk = 0))

        while (queue.isNotEmpty()) {
            val head = queue.poll()

            // Check if we've already been here. Since we're prioritizing the head
            // with the least cumulative risk, it means we've got here by some less
            // risky path.
            if (!traversedTiles.add(head.point)) continue

            // Reached the destination; remember the result and check the other routes.
            if (head.point == destination) return head

            head.point
                .surroundingTiles()
                .filter(riskMap.keys::contains)
                .filterNot(traversedTiles::contains)
                .map { Head(it, cumulativeRisk = head.cumulativeRisk + riskMap.getValue(it)) }
                .let(queue::addAll)
        }

        throw IllegalStateException("Impossible!")
    }

    private fun Point.surroundingTiles() = sequence {
        yield(Point(x + 1, y))
        yield(Point(x - 1, y))
        yield(Point(x, y + 1))
        yield(Point(x, y - 1))
    }

    private fun parse(input: List<String>): Map<Point, Int> = buildMap {
        input.forEachIndexed { y, line ->
            line.forEachIndexed { x, number ->
                put(Point(x, y), number - '0')
            }
        }
    }

    private data class Head(val point: Point, val cumulativeRisk: Int = 0)
}
