package org.chalup.advent2022

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.plus
import kotlin.math.absoluteValue

object Day9 {
    fun task1(input: List<String>) = input
        .let(::parseMoves)
        .let { simulateRopeTail(it, numberOfKnots = 2) }
        .count()

    fun task2(input: List<String>) = input
        .let(::parseMoves)
        .let { simulateRopeTail(it, numberOfKnots = 10) }
        .count()
}

private fun parseMoves(input: List<String>): Sequence<Direction> = input
    .asSequence()
    .flatMap {
        it.split(' ').let { (directionChar, steps) ->
            sequence {
                val direction = Direction.fromSymbol(directionChar)
                repeat(steps.toInt()) { yield(direction) }
            }
        }
    }

private fun simulateRopeTail(moves: Sequence<Direction>, numberOfKnots: Int): Sequence<Point> =
    moves
        .scan(State(numberOfKnots)) { state, direction ->
            State(
                buildList<Point> {
                    for (knot in state.knots) {
                        add(
                            when {
                                isEmpty() -> knot + direction.vector
                                knot isCloseTo last() -> knot
                                else -> knot moveTowards last()
                            }
                        )
                    }
                }
            )
        }
        .map { it.knots.last() }
        .distinct()

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

private data class State(val knots: List<Point>) {
    constructor(n: Int) : this(List(n) { Point(0, 0) })
}