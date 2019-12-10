package org.chalup.advent2019

import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.Vector
import org.chalup.utils.contains
import org.chalup.utils.manhattanDistance
import org.chalup.utils.plus
import org.chalup.utils.points

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

    private fun detectedAsteroids(origin: Point, asteroids: List<Point>, mapBounds: Rect): List<Point> {
        val asteroidsLeft = asteroids.toMutableSet().apply { remove(origin) }
        val cellsToCheck = mapBounds.points().toMutableList().apply {
            remove(origin)
            sortBy { manhattanDistance(origin, it) }
        }

        val detected = mutableListOf<Point>()

        while (asteroidsLeft.isNotEmpty() && cellsToCheck.isNotEmpty()) {
            val scannedCells = ray(origin, Vector(origin, cellsToCheck.first()))
                .takeWhile { it in mapBounds }
                .toList()

            val detectedAsteroid = scannedCells.firstOrNull { it in asteroidsLeft }
            if (detectedAsteroid != null) detected += detectedAsteroid

            asteroidsLeft.removeAll(scannedCells)
            cellsToCheck.removeAll(scannedCells)
        }

        return detected
    }

    fun maximumNumberOfDetectedAsteroids(input: List<String>) = input
        .let(this::readMap)
        .let { (bounds, asteroids) ->
            asteroids
                .asSequence()
                .map {
                    detectedAsteroids(origin = it,
                                      asteroids = asteroids,
                                      mapBounds = bounds)
                }
                .map { it.size }
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