package org.chalup.advent2022

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.plus
import kotlin.math.absoluteValue

object Day9 {
    fun task1(input: List<String>) = input
        .asSequence()
        .flatMap {
            it.split(' ').let { (directionChar, steps) ->
                sequence {
                    val direction = Direction.fromSymbol(directionChar)
                    repeat(steps.toInt()) { yield(direction) }
                }
            }
        }
        .scan(State()) { state, direction ->
            val newHeadLocation = state.head + direction.vector

            val newTailLocation = when {
                state.tail isCloseTo newHeadLocation -> state.tail
                else -> state.tail moveTowards newHeadLocation
            }

            State(newHeadLocation, newTailLocation)
        }
        .distinctBy { (_, tail) -> tail }
        .count()
}

private infix fun Point.moveTowards(other: Point): Point {
    val v = Vector(this, other).let { (dx, dy) ->
        Vector(
            dx.coerceIn(-1..1),
            dy.coerceIn(-1..1),
        )
    }

    return this + v
}

private infix fun Point.isCloseTo(other: Point): Boolean {
    return (this.x - other.x).absoluteValue <= 1 && (this.y - other.y).absoluteValue <= 1
}

private data class State(val head: Point = Point(0, 0), val tail: Point = head)