package org.chalup.advent2016

import org.chalup.advent2016.Day1.Rotation.CLOCKWISE
import org.chalup.advent2016.Day1.Rotation.COUNTERCLOCKWISE
import org.chalup.advent2016.Day1.Rotation.values
import org.chalup.advent2018.cycleNext
import org.chalup.advent2018.cyclePrev
import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.manhattanDistance
import org.chalup.utils.match
import org.chalup.utils.plus

object Day1 {
    fun task1(input: List<String>): Int = input
        .let(this::parseInstructions)
        .let(this::visitedLocations)
        .last()
        .let { manhattanDistance(Point(0, 0), it) }

    fun task2(input: List<String>): Int = input
        .let(this::parseInstructions)
        .let(this::visitedLocations)
        .firstDuplicate()
        .let { manhattanDistance(Point(0, 0), it) }

    private fun <T> Sequence<T>.firstDuplicate(): T {
        val set = mutableSetOf<T>()

        for (element in this) {
            if (!set.add(element)) return element
        }

        throw NoSuchElementException()
    }

    private fun visitedLocations(steps: Sequence<Step>) = sequence {
        var position = Position(Bearing.NORTH, Point(0, 0))

        yield(position.location)

        for (step in steps) {
            position = position.copy(bearing = position.bearing + step.rotation)

            repeat(step.stepsForward) {
                position = position.copy(location = position.location + position.bearing.vector)
                yield(position.location)
            }
        }
    }

    private fun parseInstructions(input: List<String>) = input
        .single()
        .splitToSequence(",")
        .map(this::parseStep)

    private fun parseStep(text: String) = match<Step>(text.trim()) {
        pattern("""(L|R)(\d+)""") { (rotationSymbol, steps) ->
            Step(
                rotation = values().first { it.symbol == rotationSymbol.single() },
                stepsForward = steps.toInt()
            )
        }
    }

    private data class Position(val bearing: Bearing, val location: Point)

    private data class Step(val rotation: Rotation, val stepsForward: Int)

    @Suppress("unused")
    enum class Bearing(val vector: Vector) {
        NORTH(Vector(0, 1)),
        EAST(Vector(1, 0)),
        SOUTH(Vector(0, -1)),
        WEST(Vector(-1, 0))
    }

    private enum class Rotation(val symbol: Char) {
        CLOCKWISE('R'), COUNTERCLOCKWISE('L')
    }

    private operator fun Bearing.plus(rotation: Rotation): Bearing = when (rotation) {
        CLOCKWISE -> cycleNext()
        COUNTERCLOCKWISE -> cyclePrev()
    }
}