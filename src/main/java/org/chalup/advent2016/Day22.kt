package org.chalup.advent2016

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.bounds
import org.chalup.utils.plus
import java.util.LinkedList

object Day22 {
    fun task1(input: List<String>): Int {
        val nodes = parseNodes(input)

        return nodes.sumOf { n1 ->
            nodes.count { n2 ->
                n1.used > 0 && n1 != n2 && (n2.size - n2.used) >= n1.used
            }
        }
    }

    // If you dump the data, it turns out that:
    //
    // a) there's a single empty node you can start from
    // b) there's a couple of nodes that create an impassable wall
    // c) you can easily shuffle the data between all the other nodes
    //
    // There's no need to solve the generic case with any data arrangement.
    // First, we need to move the empty node next to the target node, navigating
    // around the impassable wall. Simple flood fill will suffice. Then, we can
    // shuffle the goal data along the top edge.
    fun task2(input: List<String>): Int {
        val nodes = parseNodes(input)

        val nodesByLocation = nodes.associateBy { it.location }
        val clusterBounds = nodesByLocation.keys.bounds()
        val targetNode = nodesByLocation.getValue(Point(clusterBounds.bottomRight.x, clusterBounds.topLeft.y))
        val prepNodeLocation = targetNode.location + Direction.L.vector
        val startingNode = nodes.single { it.used == 0 }

        fun preparationSteps(): Int {
            val queue = LinkedList<Pair<Point, Int>>().apply { add(startingNode.location to 0) }
            val visited = mutableSetOf<Point>()
            while (queue.isNotEmpty()) {
                val (p, steps) = queue.poll()

                if (!visited.add(p)) continue
                if (p == prepNodeLocation) return steps

                val currentNode = nodesByLocation.getValue(p)

                Direction.entries.mapNotNullTo(queue) { d ->
                    (p + d.vector)
                        .takeIf {
                            val node = nodesByLocation[it]

                            if (node == null) {
                                false
                            } else {
                                node.used < currentNode.size
                            }
                        }
                        ?.let { it to steps + 1 }
                }
            }
            throw IllegalStateException("Could not find the path to the left neighbour of target node")
        }

        val shuffleSteps = (clusterBounds.width - 2) * (1 + 4)

        return preparationSteps() + shuffleSteps + 1
    }

    private fun parseNodes(input: List<String>) = input.drop(2).map {
        """/dev/grid/node-x(\d+)-y(\d+)\s+(\d+)T\s+(\d+)T.*"""
            .toRegex()
            .matchEntire(it)!!
            .groupValues
            .drop(1)
            .map(String::trim)
            .map(String::toInt)
            .let { (x, y, size, used) -> Node(location = Point(x, y), size, used) }
    }

    private data class Node(
        val location: Point,
        val size: Int,
        val used: Int
    )
}
