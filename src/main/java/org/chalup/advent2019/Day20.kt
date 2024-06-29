package org.chalup.advent2019

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.plus
import java.util.LinkedList
import java.util.PriorityQueue

object Day20 {
    fun task1(map: List<String>): Int {
        val maze = Maze(map)

        val visited = mutableSetOf<Point>()
        val queue = LinkedList<Pair<Point, Int>>().apply { add(maze.entrance to 0) }
        while (queue.isNotEmpty()) {
            val (p, steps) = queue.poll()

            if (!visited.add(p)) continue
            if (p == maze.exit) return steps

            Direction.entries.mapNotNullTo(queue) { d ->
                (p + d.vector)
                    .takeIf(maze::isCorridor)
                    ?.let { it to steps + 1 }
            }

            val warp = maze.portals[p]
            if (warp != null) {
                queue += warp to steps + 1
            }
        }

        throw IllegalStateException("Could not find the solution :(")
    }

    fun task2(map: List<String>): Int {
        val maze = Maze(map)

        val paths: Map<Point, Map<Point, Int>> = (maze.portals.keys + maze.entrance)
            .associateWith { from ->
                buildMap<Point, Int> {
                    val visited = mutableSetOf<Point>()

                    val queue = LinkedList<Pair<Point, Int>>().apply { add(from to 0) }
                    while (queue.isNotEmpty()) {
                        val (p, steps) = queue.poll()

                        if (!visited.add(p)) continue

                        if (p != from && (p in maze.portals || p == maze.exit)) put(p, steps)

                        Direction.entries.mapNotNullTo(queue) { d ->
                            (p + d.vector)
                                .takeIf(maze::isCorridor)
                                ?.let { it to steps + 1 }
                        }
                    }
                }
            }

        fun portalId(p: Point) = "${if (maze.isOuterPortal(p)) "outer" else "inner"} ${maze.portalName(p)}"

        data class Location(val point: Point, val level: Int) {
            override fun toString(): String = "${portalId(point)} lvl $level"
        }

        val visited = mutableSetOf<Location>()
        val queue = PriorityQueue<Pair<Location, Int>>(compareBy<Pair<Location, Int>> { (_, steps) -> steps }).apply {
            add(Location(maze.entrance, level = 0) to 0)
        }
        var skipped = 0
        while (queue.isNotEmpty()) {
            val (l, steps) = queue.poll()

            if (!visited.add(l)) {
                skipped++
                continue
            }

            for ((otherPortal, stepsToOtherPortal) in paths.getValue(l.point)) {
                if (l.level == 0 && maze.isOuterPortal(otherPortal)) {
                    if (otherPortal == maze.exit) return steps + stepsToOtherPortal
                } else if (otherPortal != maze.exit) {
                    val levelChange = if (maze.isOuterPortal(otherPortal)) -1 else +1
                    queue.add(
                        Location(
                            point = maze.portals.getValue(otherPortal),
                            level = l.level + levelChange
                        ) to (steps + stepsToOtherPortal + 1)
                    )
                }
            }
        }

        throw IllegalStateException("Could not find the solution :(")
    }

    private class Maze(private val map: List<String>) {
        fun isOuterPortal(p: Point): Boolean = when {
            p.y == 2 -> true
            p.x == 2 -> true
            p.y == map.size - 3 -> true
            p.x == map.first().length - 3 -> true
            else -> false
        }

        fun portalName(p: Point) = portalNames.getValue(p)

        private val portalNames: Map<Point, String> = buildMap {
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

        val portals = portalNames.entries.groupBy { (_, name) -> name }
            .filterValues { it.size == 2 }
            .mapValues { (_, entries) -> entries.map { (point, _) -> point } }
            .values
            .flatMap { (a, b) -> listOf(a to b, b to a) }
            .toMap()

        private fun findPortalByName(name: String) = portalNames.entries.mapNotNull { (p, n) -> p.takeIf { n == name } }.single()

        val entrance = findPortalByName("AA")
        val exit = findPortalByName("ZZ")

        fun isCorridor(p: Point) = map[p.y][p.x] == '.'
    }
}