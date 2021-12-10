package org.chalup.advent2021

import org.chalup.utils.match

object Day2 {
    fun task1(input: List<String>): Int = input
        .asSequence()
        .map { parsePlannedCourse(it) }
        .fold(Position()) { position, (direction, amount) ->
            when(direction) {
                Direction.FORWARD -> position.copy(horizontalDistance = position.horizontalDistance + amount)
                Direction.DOWN -> position.copy(depth = position.depth + amount)
                Direction.UP -> position.copy(depth = position.depth - amount)
            }
        }
        .let { (depth, horizontalDistance) -> depth * horizontalDistance }

    fun task2(input: List<String>): Int = input
        .asSequence()
        .map { parsePlannedCourse(it) }
        .fold(PositionWithAim()) { position, (direction, amount) ->
            when(direction) {
                Direction.FORWARD -> position.copy(
                    horizontalDistance = position.horizontalDistance + amount,
                    depth = position.depth + amount * position.aim,
                )
                Direction.DOWN -> position.copy(aim = position.aim + amount)
                Direction.UP -> position.copy(aim = position.aim - amount)
            }
        }
        .let { (depth, horizontalDistance) -> depth * horizontalDistance }

    private fun parsePlannedCourse(input: String) = match<PlannedCourse>(input) {
        Direction.values().forEach { direction ->
            pattern("""${direction.name.toLowerCase()} (\d+)""") { (amount) -> PlannedCourse(direction, amount.toInt()) }
        }
    }

    data class PlannedCourse(
        val direction: Direction,
        val amount: Int,
    )

    data class Position(
        val depth: Int = 0,
        val horizontalDistance: Int = 0,
    )

    data class PositionWithAim(
        val depth: Int = 0,
        val horizontalDistance: Int = 0,
        val aim: Int = 0,
    )

    enum class Direction {
        FORWARD, DOWN, UP
    }
}

