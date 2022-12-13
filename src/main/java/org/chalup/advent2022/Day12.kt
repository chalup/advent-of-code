package org.chalup.advent2022

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.plus
import java.util.PriorityQueue

object Day12 {
    fun task1(input: List<String>) = input
        .let(::MountainMap)
        .let { map ->
            findPath(
                map,
                startAt = map.startingPoint,
                successCondition = { it == map.endPoint },
                allowedMoveFilter = { from, to -> map[to] <= map[from] + 1 }
            )
        }
        .size
        .dec() // counting the steps taken, not visited positions

    fun task2(input: List<String>) = input
        .let(::MountainMap)
        .let { map ->
            findPath(
                map,
                startAt = map.endPoint,
                successCondition = { map[it] == 0 },
                allowedMoveFilter = { from, to -> map[to] >= map[from] - 1 }
            )
        }
        .size
        .dec() // counting the steps taken, not visited positions
}

private fun findPath(
    map: MountainMap,
    startAt: Point,
    successCondition: (Point) -> Boolean,
    allowedMoveFilter: (from: Point, to: Point) -> Boolean
): List<Point> {
    val visitedPoints = mutableSetOf<Point>()
    val paths = PriorityQueue<List<Point>>(compareBy { it.size })

    visitedPoints.add(startAt)
    paths.add(listOf(startAt))

    while (paths.isNotEmpty()) {
        val path = paths.remove()

        val head = path.last()

        Direction
            .values()
            .map { d -> head + d.vector }
            .filter { it in map }
            .filter { allowedMoveFilter(head, it) }
            .filterNot { it in visitedPoints }
            .forEach { newHead ->
                val newPath = path + newHead

                if (successCondition(newHead)) return newPath

                visitedPoints.add(newHead)
                paths.add(newPath)
            }
    }

    return emptyList()
}

private class MountainMap(private val lines: List<String>) {
    val height = lines.size
    val width = lines.first().length

    private fun find(char: Char): Point = lines
        .mapIndexedNotNull { y, line -> line.indexOf(char).takeUnless { it == -1 }?.let { x -> Point(x, y) } }
        .single()

    val startingPoint = find('S')
    val endPoint = find('E')

    operator fun get(point: Point): Int = when (val char = lines[point.y][point.x]) {
        'S' -> 'a' - 'a'
        'E' -> 'z' - 'a'
        else -> char - 'a'
    }

    operator fun contains(point: Point): Boolean = when {
        point.x < 0 -> false
        point.x >= width -> false
        point.y < 0 -> false
        point.y >= height -> false
        else -> true
    }
}