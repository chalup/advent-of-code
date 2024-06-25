package org.chalup.advent2017

import java.util.LinkedList

object Day12 {
    fun task1(input: List<String>): Int {
        val routes = input.associate { line ->
            val (src, dst) = line.split(" <-> ")

            src.toInt() to dst.split(", ").map { it.toInt() }
        }

        val visited = mutableSetOf<Int>()
        val queue = LinkedList<Int>().apply { add(0) }
        while (queue.isNotEmpty()) {
            val node = queue.poll()
            if (!visited.add(node)) continue

            queue += routes[node].orEmpty()
        }

        return visited.size
    }

    fun task2(input: List<String>): Int {
        val routes = input.associate { line ->
            val (src, dst) = line.split(" <-> ")

            src.toInt() to dst.split(", ").map { it.toInt() }
        }

        val ungroupedNodes = routes.flatMapTo(mutableSetOf()) { (from, to) -> to + from }
        var groups = 0

        while (ungroupedNodes.isNotEmpty()) {
            val visited = mutableSetOf<Int>()
            val queue = LinkedList<Int>().apply { add(ungroupedNodes.first()) }

            while (queue.isNotEmpty()) {
                val node = queue.poll()
                if (!visited.add(node)) continue

                queue += routes[node].orEmpty()
            }

            ungroupedNodes.removeAll(visited)
            groups++
        }

        return groups
    }
}