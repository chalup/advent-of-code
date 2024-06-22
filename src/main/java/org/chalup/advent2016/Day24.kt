package org.chalup.advent2016

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.plus
import java.util.LinkedList

object Day24 {
    fun task1(input: List<String>): Int = calculateNumberOfSteps(input).first
    fun task2(input: List<String>): Int = calculateNumberOfSteps(input).second

    private fun calculateNumberOfSteps(input: List<String>): Pair<Int, Int> {
        val nodes = buildMap {
            input.forEachIndexed { y, row ->
                row.forEachIndexed { x, c ->
                    if (c != '.' && c != '#') {
                        put(c, Point(x, y))
                    }
                }
            }
        }

        fun determineDistances(from: Point): Map<Char, Int> {
            val routes = mutableMapOf<Char, Int>()

            val visited = mutableSetOf<Point>()
            val queue = LinkedList<Pair<Point, Int>>().apply { add(from to 0) }

            while (queue.isNotEmpty() && routes.size < nodes.size) {
                val (p, steps) = queue.poll()
                if (!visited.add(p)) continue

                when (val tile = input[p.y][p.x]) {
                    '#', '.' -> Unit
                    else -> routes[tile] = steps
                }

                Direction.entries
                    .mapNotNullTo(queue) { d ->
                        (p + d.vector)
                            .takeIf { (x, y) -> input[y][x] != '#' }
                            ?.let { it to (steps + 1) }
                    }
            }

            return routes
        }

        val distances = nodes.mapValues { (_, p) -> determineDistances(p) }
        fun distance(from: Char, to: Char) = distances.getValue(from).getValue(to)

        data class Traversal(
            val node: Char,
            val nodesToVisit: Set<Char>,
            val stepsTaken: Int,
        )

        var best = Int.MAX_VALUE
        var bestWithCleanup = Int.MAX_VALUE

        val queue = LinkedList<Traversal>().apply {
            add(
                Traversal(
                    node = '0',
                    nodesToVisit = nodes.keys - '0',
                    stepsTaken = 0
                )
            )
        }

        while (queue.isNotEmpty()) {
            val t = queue.poll()

            if (t.stepsTaken >= best && t.stepsTaken >= bestWithCleanup) continue
            if (t.nodesToVisit.isEmpty()) {
                best = minOf(best, t.stepsTaken)
                bestWithCleanup = minOf(bestWithCleanup, t.stepsTaken + distance(t.node, '0'))
            } else {
                for (nextNode in t.nodesToVisit) {
                    queue.addFirst(
                        Traversal(
                            node = nextNode,
                            nodesToVisit = t.nodesToVisit - nextNode,
                            stepsTaken = t.stepsTaken + distance(t.node, nextNode)
                        )
                    )
                }
            }
        }

        return best to bestWithCleanup
    }
}
