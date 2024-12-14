package org.chalup.utils

import org.chalup.advent2018.gcd
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

data class Point(val x: Int, val y: Int) {
    override fun toString() = "($x, $y)"
}

data class Rect(val topLeft: Point, val bottomRight: Point) {
    constructor(left: Int, top: Int, right: Int, bottom: Int) : this(Point(left, top),
                                                                     Point(right, bottom))

    val width = bottomRight.x - topLeft.x + 1
    val height = bottomRight.y - topLeft.y + 1

    val bottom = bottomRight.y
}

fun manhattanDistance(a: Point, b: Point) = abs(a.x - b.x) + abs(a.y - b.y)

operator fun Rect.contains(point: Point) = with(point) {
    val (minX, minY) = topLeft
    val (maxX, maxY) = bottomRight

    (x in minX..maxX) && (y in minY..maxY)
}

fun Rect.points() = ((topLeft.x..bottomRight.x) * (topLeft.y..bottomRight.y)).map { (x, y) -> Point(x, y) }


fun Iterable<Point>.bounds(): Rect {
        val (minX, maxX) = map { it.x }.let { it.minOrNull()!! to it.maxOrNull()!! }
        val (minY, maxY) = map { it.y }.let { it.minOrNull()!! to it.maxOrNull()!! }

    return Rect(minX, minY, maxX, maxY)
}

infix fun Point.isOnTheEdgeOf(bounds: Rect) = with(bounds) {
    when {
        x == topLeft.x -> true
        x == bottomRight.x -> true
        y == topLeft.y -> true
        y == bottomRight.y -> true
        else -> false
    }
}

data class Vector(val dx: Int, val dy: Int) {
    constructor(from: Point, to: Point) : this(dx = to.x - from.x,
                                               dy = to.y - from.y)
}

fun Vector.normalized() = gcd(abs(dx), abs(dy)).let { gcd -> Vector(dx / gcd, dy / gcd) }
fun Vector.toAngle(): Double = atan2(dy.toDouble(), dx.toDouble())

operator fun Point.plus(velocity: Vector) = with(velocity) { Point(x + dx, y + dy) }
operator fun Point.minus(velocity: Vector) = with(velocity) { Point(x - dx, y - dy) }
operator fun Vector.times(magnitude: Int) = copy(dx = dx * magnitude, dy = dy * magnitude)
operator fun Vector.unaryMinus(): Vector = this * (-1)

enum class Direction(val symbol: String, val vector: Vector) {
    U("U", Vector(0, -1)),
    R("R", Vector(+1, 0)),
    D("D", Vector(0, +1)),
    L("L", Vector(-1, 0));

    companion object {
        fun fromSymbol(symbol: String) = values().firstOrNull { it.symbol == symbol } ?: throw IllegalArgumentException("Unrecognized direction: $symbol")
    }
}

val Direction.opposite: Direction
    get() = when (this) {
        Direction.U -> Direction.D
        Direction.R -> Direction.L
        Direction.D -> Direction.U
        Direction.L -> Direction.R
    }
