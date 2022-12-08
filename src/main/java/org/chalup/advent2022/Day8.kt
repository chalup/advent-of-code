package org.chalup.advent2022

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.plus

object Day8 {
    fun task1(input: List<String>) = ForestMap(input)
        .let { map ->
            val rays = buildList {
                (0 until map.width).forEach { x ->
                    add(Point(x, 0) to Vector(0, 1))
                    add(Point(x, map.height - 1) to Vector(0, -1))
                }
                (0 until map.height).forEach { y ->
                    add(Point(0, y) to Vector(1, 0))
                    add(Point(map.width - 1, y) to Vector(-1, 0))
                }
            }

            buildSet {
                for ((startingPoint, vector) in rays) {
                    var currentHeight = -1
                    var point = startingPoint
                    do {
                        val height = map[point]
                        if (height > currentHeight) {
                            add(point)
                            currentHeight = height
                        }
                        point += vector
                    } while (point in map)
                }
            }
        }
        .count()

    fun task2(input: List<String>) = ForestMap(input)
        .let { map ->
            val points = (0 until map.width).flatMap { x -> (0 until map.height).map { y -> Point(x, y) } }

            fun ray(from: Point, direction: Direction) = sequence {
                var point = from + direction.vector
                while (point in map) {
                    yield(point)
                    if (map[point] >= map[from]) break
                    point += direction.vector
                }
            }

            points.maxOf { point ->
                Direction.values()
                    .map { d -> ray(point, d).count() }
                    .fold(1, Int::times)
            }
        }
}

class ForestMap(private val lines: List<String>) {
    val width: Int = lines.first().length
    val height: Int = lines.size

    operator fun get(point: Point): Int = lines[point.y][point.x] - '0'
    operator fun contains(point: Point): Boolean = when {
        point.x < 0 -> false
        point.x >= width -> false
        point.y < 0 -> false
        point.y >= height -> false
        else -> true
    }
}