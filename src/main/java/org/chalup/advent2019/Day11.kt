package org.chalup.advent2019

import org.chalup.advent2018.cycleNext
import org.chalup.advent2018.cyclePrev
import org.chalup.advent2019.Day11.Rotation.CLOCKWISE
import org.chalup.advent2019.Day11.Rotation.COUNTERCLOCKWISE
import org.chalup.advent2019.IntcodeInterpreter.Companion.parseProgram
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.ExecutionError
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.Finished
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.GeneratedOutput
import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.bounds
import org.chalup.utils.plus

object Day11 {
    fun paintedPanels(paintingProgram: String, startingPanelColor: Long = 0): Map<Point, Long> {
        var position = Point(0, 0)
        var direction = Direction.U

        val panels = mutableMapOf(position to startingPanelColor)

        val interpreter = IntcodeInterpreter(parseProgram(paintingProgram))

        while (true) {
            interpreter.sendInput(panels[position] ?: 0)

            val paintColor = interpreter.step() ?: break
            val rotation = interpreter.step()?.let { if (it == 0L) COUNTERCLOCKWISE else CLOCKWISE } ?: break

            panels[position] = paintColor
            direction += rotation
            position += direction.vector
        }

        return panels
    }

    operator fun Direction.plus(rotation: Rotation): Direction = when (rotation) {
        CLOCKWISE -> cycleNext()
        COUNTERCLOCKWISE -> cyclePrev()
    }

    enum class Rotation { CLOCKWISE, COUNTERCLOCKWISE }

    private fun IntcodeInterpreter.step() = when (val result = run()) {
        is ExecutionError -> throw RuntimeException(result.getErrorMessage())
        is GeneratedOutput -> result.output
        is Finished -> null
    }

    fun task1(program: String) = paintedPanels(program).keys.size

    fun task2(program: String) = paintedPanels(program, startingPanelColor = 1)
        .filter { (_, color) -> color == 1L }
        .keys
        .let { whitePanels ->
            val bounds = whitePanels.bounds()

            val result = StringBuilder("\n")
            for (y in bounds.topLeft.y..bounds.bottomRight.y) {
                for (x in bounds.topLeft.x..bounds.bottomRight.x) {
                    result.append(if (whitePanels.contains(Point(x, y))) '#'
                                  else ' ')
                }
                result.append("\n")
            }
            result.toString()
        }
}
