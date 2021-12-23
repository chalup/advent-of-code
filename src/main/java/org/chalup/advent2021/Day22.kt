package org.chalup.advent2021

import org.chalup.utils.Vector3
import org.chalup.utils.match

object Day22 {
    fun task1(input: List<String>): Int = input
        .map(this::parseInstruction)
        .filter { it.isInitializationInstruction() }
        .fold(setOf<Vector3>()) { enabledPoints, (enable, cube) ->
            if (enable) {
                enabledPoints + cube.points()
            } else {
                enabledPoints - cube.points()
            }
        }
        .size

    private data class Cube(
        val xs: LongRange,
        val ys: LongRange,
        val zs: LongRange,
    ) {
        fun points() = buildSet {
            for (x in xs)
                for (y in ys)
                    for (z in zs)
                        add(Vector3(x, y, z))
        }
    }

    private data class Instruction(
        val enable: Boolean,
        val cube: Cube,
    )

    private fun parseInstruction(line: String): Instruction = match(line) {
        pattern("""(on|off) x=$RANGE,y=$RANGE,z=$RANGE""") { (onOff, x1, x2, y1, y2, z1, z2) ->
            Instruction(
                enable = onOff == "on",
                cube = Cube(
                    xs = (x1.toLong())..(x2.toLong()),
                    ys = (y1.toLong())..(y2.toLong()),
                    zs = (z1.toLong())..(z2.toLong()),
                )
            )
        }
    }

    private fun Instruction.isInitializationInstruction() = cube.xs.isInInitializationRange() &&
            cube.ys.isInInitializationRange() &&
            cube.xs.isInInitializationRange()

    private fun LongRange.isInInitializationRange() = when {
        first < -50 -> false
        last > 50 -> false
        else -> true
    }

    private const val DIGIT = """([-\d]+)"""
    private const val RANGE = """$DIGIT..$DIGIT"""
}
