package org.chalup.advent2019

import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.Vector
import org.chalup.utils.manhattanDistance
import org.chalup.utils.normalized
import org.chalup.utils.plus
import org.chalup.utils.toAngle
import kotlin.math.PI

object Day10 {
    data class AsteroidsMap(val bounds: Rect,
                            val asteroids: List<Point>)

    fun readMap(lines: List<String>) = AsteroidsMap(bounds = Rect(left = 0,
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

    fun vaporizationOrder(laserLocation: Point, asteroids: List<Point>): List<Point> {
        data class VaporizationInfo(val angle: Double,
                                    val vaporizationCycle: Int,
                                    val location: Point)
        return asteroids
            .filter { it != laserLocation }
            .groupBy { Vector(laserLocation, it).normalized() }
            .flatMap { (vector, asteroids) ->
                asteroids
                    .sortedBy { manhattanDistance(laserLocation, it) }
                    .mapIndexed { index, point ->
                        VaporizationInfo(angle = (vector.toAngle() + 0.5 * PI + 2.0 * PI) % (2.0 * PI),
                                         vaporizationCycle = index,
                                         location = point)
                    }
            }
            .sortedWith(compareBy<VaporizationInfo> { it.vaporizationCycle }.thenBy { it.angle })
            .map { it.location }
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

    fun vaporizationBet(input: List<String>, nth: Int): Int {
        val (bounds, asteroids) = readMap(input)

        val laserLocation = asteroids
            .asSequence()
            .maxBy { countDetectedAsteroids(origin = it,
                                            asteroids = asteroids,
                                            mapBounds = bounds) }!!

        val nthAsteroid = vaporizationOrder(laserLocation, asteroids)[nth - 1]

        return nthAsteroid.x * 100 + nthAsteroid.y
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