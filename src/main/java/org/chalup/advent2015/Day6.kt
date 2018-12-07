package org.chalup.advent2015

import org.chalup.advent2015.Day6.Command.Off
import org.chalup.advent2015.Day6.Command.On
import org.chalup.advent2015.Day6.Command.Toggle
import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.contains
import org.chalup.utils.times

typealias CommandHandler<T> = Day6.Light<T>.(Day6.Command) -> Day6.Light<T>

object Day6 {
    data class Light<T>(val position: Point, val status: T)

    sealed class Command {
        abstract val range: Rect

        data class On(override val range: Rect) : Command()
        data class Off(override val range: Rect) : Command()
        data class Toggle(override val range: Rect) : Command()
    }

    class Grid<T> private constructor(val lights: List<Light<T>>, private val handleCommand: CommandHandler<T>) {
        constructor(width: Int, height: Int, initialStatus: T, handleCommand: CommandHandler<T>) : this(
            lights = ((0 until width) * (0 until height)).map { (x, y) -> Light(Point(x, y), initialStatus) },
            handleCommand = handleCommand
        )

        fun apply(command: Command) = Grid(
            lights.map { light ->
                when {
                    light.position in command.range -> light.handleCommand(command)
                    else -> light
                }
            },
            handleCommand
        )
    }

    private val commandRegex = """(.*?) (\d+),(\d+) through (\d+),(\d+)""".toRegex()

    fun String.parse(): Command = commandRegex
        .matchEntire(this)!!
        .groupValues.drop(1)
        .let { data ->
            val command = data.first()
            val range = data.drop(1).map { it.toInt() }.let { (left, top, right, bottom) ->
                Rect(Point(left, top), Point(right, bottom))
            }

            when (command) {
                "turn on" -> On(range)
                "turn off" -> Off(range)
                "toggle" -> Toggle(range)
                else -> throw UnsupportedOperationException("Unknown command '$command'")
            }
        }

    fun countTurnedOnLightsAfterApplyingAllCommands(input: List<String>) = input
        .map { it.parse() }
        .fold(Grid(1000, 1000,
                   initialStatus = false,
                   handleCommand = { command ->
                       when (command) {
                           is On -> copy(status = true)
                           is Off -> copy(status = false)
                           is Toggle -> copy(status = !status)
                       }
                   })) { grid, command -> grid.apply(command) }
        .lights.count { it.status }

    fun determineGridBrightnessAfterApplyingAllCommands(input: List<String>) = input
        .map { it.parse() }
        .fold(Grid(1000, 1000,
                   initialStatus = 0,
                   handleCommand = { command ->
                       when (command) {
                           is On -> copy(status = status + 1)
                           is Off -> copy(status = (status - 1).coerceAtLeast(0))
                           is Toggle -> copy(status = status + 2)
                       }
                   })) { grid, command -> grid.apply(command) }
        .lights.sumBy { it.status }
}