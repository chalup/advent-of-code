package org.chalup.advent2020

import org.chalup.advent2019.Day11.Rotation
import org.chalup.advent2019.Day11.Rotation.CLOCKWISE
import org.chalup.advent2019.Day11.Rotation.COUNTERCLOCKWISE
import org.chalup.advent2019.Day11.plus
import org.chalup.advent2020.FerryInstruction.*
import org.chalup.utils.*

object Day12 {
    fun task1(input: List<String>): Int = input
        .map(::parseInstruction)
        .fold(FerryState(location = Point(0, 0), heading = Direction.R)) { prev, instruction ->
            when (instruction) {
                is Translate -> prev.copy(location = prev.location + (instruction.direction.vector * instruction.amount))
                is Rotate -> (0 until instruction.steps).fold(prev) { f, _ -> f.copy(heading = f.heading + instruction.rotation) }
                is MoveForward -> prev.copy(location = prev.location + (prev.heading.vector * instruction.amount))
            }
        }
        .let { (finalLocation, _) ->
            manhattanDistance(Point(0, 0), finalLocation)
        }

    fun task2(input: List<String>): Int = input
        .map(::parseInstruction)
        .fold(FerryWithWaypoint(ferryLocation = Point(0, 0), waypointVector = Vector(10, 1))) { prev, instruction ->
            when (instruction) {
                is Translate -> prev.copy(waypointVector = prev.waypointVector + (instruction.direction.vector * instruction.amount))
                is Rotate -> prev.copy(waypointVector = (0 until instruction.steps).fold(prev.waypointVector) { v, _ -> v rotate instruction.rotation })
                is MoveForward -> prev.copy(ferryLocation = prev.ferryLocation + (prev.waypointVector * instruction.amount))
            }
        }
        .let { (finalFerryLocation, _) ->
            manhattanDistance(Point(0, 0), finalFerryLocation)
        }
}

private infix fun Vector.rotate(rotation: Rotation) = when (rotation) {
    CLOCKWISE -> Vector(dx = dy, dy = -dx)
    COUNTERCLOCKWISE -> Vector(dx = -dy, dy = dx)
}

private fun parseInstruction(instruction: String) = match<FerryInstruction>(instruction) {
    pattern("""([NSEW])(\d+)""") { (directionSymbol, amount) ->
        val direction = when (directionSymbol) {
            "N" -> Direction.D
            "S" -> Direction.U
            "E" -> Direction.R
            "W" -> Direction.L
            else -> throw IllegalArgumentException("Unexpected direction symbol: $directionSymbol")
        }

        Translate(direction, amount.toInt())
    }
    pattern("""([RL])(\d+)""") { (rotationSymbol, degrees) ->
        val rotation = when (rotationSymbol) {
            "R" -> CLOCKWISE
            "L" -> COUNTERCLOCKWISE
            else -> throw IllegalArgumentException("Unexpected rotation symbol: $rotationSymbol")
        }
        Rotate(rotation, steps = degrees.toInt().also { check(it % 90 == 0) }.div(90))
    }
    pattern("""F(\d+)""") { (amount) -> MoveForward(amount.toInt()) }
}

private operator fun Vector.times(amount: Int) = Vector(dx = dx * amount, dy = dy * amount)
private operator fun Vector.plus(vector: Vector) = Vector(dx = dx + vector.dx, dy = dy + vector.dy)

data class FerryState(val location: Point, val heading: Direction)
data class FerryWithWaypoint(val ferryLocation: Point, val waypointVector: Vector)

sealed class FerryInstruction {
    data class Translate(val direction: Direction, val amount: Int) : FerryInstruction()
    data class Rotate(val rotation: Rotation, val steps: Int) : FerryInstruction()
    data class MoveForward(val amount: Int) : FerryInstruction()
}
