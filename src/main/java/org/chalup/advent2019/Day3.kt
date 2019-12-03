package org.chalup.advent2019

import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.manhattanDistance
import org.chalup.utils.match
import org.chalup.utils.plus

object Day3 {
    private data class WireSpec(val direction: WireDirection, val length: Int)

    private enum class WireDirection(val symbol: String, val vector: Vector) {
        L("L", Vector(-1, 0)),
        R("R", Vector(+1, 0)),
        U("U", Vector(0, +1)),
        D("D", Vector(0, -1));

        companion object {
            fun fromSymbol(symbol: String) = values().firstOrNull { it.symbol == symbol } ?: throw IllegalArgumentException("Unrecognized direction: $symbol")
        }
    }

    private data class Wire(val points: List<Point>)

    private fun parseSegment(segment: String): WireSpec = match(segment) {
        pattern("""(.)(\d+)""") { (direction, numbers) ->
            WireSpec(
                direction = WireDirection.fromSymbol(direction),
                length = numbers.toInt())
        }
    }

    private fun parseWire(wire: String): Wire = wire
        .split(",")
        .asSequence()
        .map(this::parseSegment)
        .let { specs ->
            sequence<Point> {
                var p = origin

                suspend fun SequenceScope<Point>.yieldPoint(point: Point) {
                    yield(point)
                    p = point
                }

                specs.forEach { (direction, length) -> repeat(length) { yieldPoint(p + direction.vector) } }
            }
        }
        .let { Wire(it.toList()) }


    private fun List<Wire>.intersections() = this
        .map { it.points as Iterable<Point> }
        .reduce { a, b -> a intersect b }

    fun distanceToNearestIntersection(input: List<String>): Int = input
        .map(this::parseWire)
        .intersections()
        .map { manhattanDistance(it, origin) }
        .min()!!

    fun shortestPath(input: List<String>): Int = input
        .map(this::parseWire)
        .let { wires ->
            wires
                .intersections()
                .map { intersection -> wires.sumBy { wire -> wire.points.indexOf(intersection) + 1 } }
                .min()!!
        }

    private val origin = Point(0, 0)
}