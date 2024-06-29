package org.chalup.advent2019

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.plus
import java.util.LinkedList

object Day20 {
    fun task1(map: List<String>): Int {
        val portalEntrances: Map<Point, String> = buildMap {
            map.forEachIndexed { y, line ->
                line.windowed(3).forEachIndexed { index, s ->
                    if (s.substring(0..1).all { it.isUpperCase() } && s[2] == '.') {
                        put(Point(index + 2, y), s.substring(0..1))
                    }
                    if (s.substring(1..2).all { it.isUpperCase() } && s[0] == '.') {
                        put(Point(index, y), s.substring(1..2))
                    }
                }
            }

            map.first().indices.forEach { x ->
                val column = map.map { line -> line[x] }.joinToString("")

                column.windowed(3).forEachIndexed { index, s ->
                    if (s.substring(0..1).all { it.isUpperCase() } && s[2] == '.') {
                        put(Point(x, index + 2), s.substring(0..1))
                    }
                    if (s.substring(1..2).all { it.isUpperCase() } && s[0] == '.') {
                        put(Point(x, index), s.substring(1..2))
                    }
                }
            }
        }

        val portals = portalEntrances.entries.groupBy { (_, name) -> name }
            .filterValues { it.size == 2 }
            .mapValues { (_, entries) -> entries.map { (point, _) -> point } }
            .values
            .flatMap { (a, b) -> listOf(a to b, b to a) }
            .toMap()

        fun findPortal(name: String) = portalEntrances.entries.mapNotNull { (p, n) -> p.takeIf { n == name } }.single()

        val entrance = findPortal("AA")
        val exit = findPortal("ZZ")

        val visited = mutableSetOf<Point>()
        val queue = LinkedList<Pair<Point, Int>>().apply { add(entrance to 0) }
        while (queue.isNotEmpty()) {
            val (p, steps) = queue.poll()

            if (!visited.add(p)) continue
            if (p == exit) return steps

            Direction.entries.mapNotNullTo(queue) { d ->
                (p + d.vector)
                    .takeIf { map[it.y][it.x] == '.' }
                    ?.let { it to steps + 1 }
            }

            val warp = portals[p]
            if (warp != null) {
                queue += warp to steps + 1
            }
        }

        throw IllegalStateException("Could not find the solution :(")
    }
}