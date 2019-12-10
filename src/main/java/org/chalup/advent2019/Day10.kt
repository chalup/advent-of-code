package org.chalup.advent2019

import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.Vector
import org.chalup.utils.normalized
import org.chalup.utils.plus

object Day10 {
    data class AsteroidsMap(val bounds: Rect,
                            val asteroids: List<Point>)

    private fun readMap(lines: List<String>) = AsteroidsMap(bounds = Rect(left = 0,
                                                                          top = 0,
                                                                          right = lines.first().length - 1,
                                                                          bottom = lines.count() - 1),
                                                            asteroids = sequence {
                                                                lines.forEachIndexed { y, row ->
                                                                    row.forEachIndexed { x, cell ->
                                                                        if (cell == '#') yield(Point(x, y))
                                                                    }
                                                                }
                                                            }.toList())

    private fun ray(from: Point, vector: Vector): Sequence<Point> = generateSequence(from) { p -> p + vector }

    private fun countDetectedAsteroids(origin: Point, asteroids: List<Point>, mapBounds: Rect): Int {
        return asteroids
            .asSequence()
            .filter { it != origin }
            .map { Vector(origin, it).normalized() }
            .distinct()
            .count()
    }

    fun maximumNumberOfDetectedAsteroids(input: List<String>) = input
        .let(this::readMap)
        .let { (bounds, asteroids) ->
            asteroids
                .asSequence()
                .map {
                    countDetectedAsteroids(origin = it,
                                           asteroids = asteroids,
                                           mapBounds = bounds)
                }
                .max()!!
        }

    private fun dumpMap(location: Point, detected: List<Point>, asteroids: List<Point>, bounds: Rect) {
        println("--- Station at $location detects ${detected.size} asteroids ---")
        val detectedSet = detected.toSet()
        val obscuredSet = asteroids.toSet() - detectedSet

        for (y in 0 until bounds.height) {
            for (x in 0 until bounds.width) {
                val point = Point(x, y)
                val char = when {
                    point == location -> 'X'
                    point in detectedSet -> '#'
                    point in obscuredSet -> '•'
                    else -> ' '
                }
                print(char)
            }
            println()
        }
    }
}