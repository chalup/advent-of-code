package org.chalup.advent2018

import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.bounds
import org.chalup.utils.plus

object Day10 {
    data class Star(val location: Point,
                    val velocity: Vector)

    data class Observation(val time: Int,
                           val stars: List<Star>)

    private val starRegex = """position=<(.*?),(.*?)> velocity=<(.*?),(.*?)>""".toRegex()

    private fun parse(star: String): Star =
        starRegex
            .matchEntire(star)!!
            .groupValues.drop(1)
            .map { it.trim().toInt() }
            .let { (x, y, dx, dy) ->
                Star(Point(x, y),
                     Vector(dx, dy))
            }

    fun decipherMessage(stars: List<String>): Observation = stars
        .map { parse(it) }
        .let { Observation(time = 0, stars = it) }
        .let { initialObservation ->
            generateSequence(initialObservation) { observation ->
                observation.copy(time = observation.time + 1,
                                 stars = observation.stars.map { star -> star.copy(location = star.location + star.velocity) })
            }
        }
        .zipWithNext()
        .takeWhile { (previous, current) -> observationsConverge(previous, current) }
        .last()
        .let { (_, converged) -> converged }

    private fun observationsConverge(previous: Observation, current: Observation) =
        current.stars.span() <= previous.stars.span()

    private fun List<Star>.span(): Long = map { it.location }
        .bounds()
        .let { it.width.toLong() * it.height.toLong() }
}
