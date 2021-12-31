package org.chalup.advent2021

import org.chalup.utils.Vector3
import org.chalup.utils.match

object Day22 {
    fun task1(input: List<String>): Long = input
        .map(this::parseInstruction)
        .filter { it.isInitializationInstruction() }
        .fold(emptySet<Cube>()) { cubes, instruction -> cubes.plus(instruction) }
        .fold(0L) { count, cube -> count + with(cube) { xs.size() * ys.size() * zs.size() } }

    fun task2(input: List<String>): Long = input
        .map(this::parseInstruction)
        .fold(emptySet<Cube>()) { cubes, instruction -> cubes.plus(instruction) }
        .fold(0L) { count, cube -> count + with(cube) { xs.size() * ys.size() * zs.size() } }

    internal data class Cube(
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

    // the set consists of disjointed cubes
    private fun Set<Cube>.plus(instruction: Instruction): Set<Cube> = buildSet {
        for (cube in this@plus) {
            when {
                // The cube from instruction is already accounted for; the set consists of disjointed cubes,
                // so it won't change, and we can return immediately.
                instruction.cube in cube && instruction.enable -> return this@plus
                // The cube will be either overwritten by a larger cube (if enable == true) or removed (if enabled == false)
                cube in instruction.cube -> continue
                cube intersects instruction.cube -> addAll(cube - instruction.cube)
                else -> add(cube)
            }
        }

        if (instruction.enable) add(instruction.cube)
    }

    internal infix operator fun Cube.contains(other: Cube): Boolean = when {
        other.xs !in this.xs -> false
        other.ys !in this.ys -> false
        other.zs !in this.zs -> false
        else -> true
    }

    internal infix operator fun LongRange.contains(other: LongRange): Boolean =
        other.first >= this.first && other.last <= this.last

    internal infix fun Cube.intersects(other: Cube): Boolean =
        other.xs intersects this.xs && other.ys intersects this.ys && other.zs intersects this.zs

    internal infix fun LongRange.intersects(other: LongRange): Boolean =
        this.first in other || this.last in other || other.first in this || other.last in this

    internal operator fun Cube.minus(other: Cube): Set<Cube> = buildSet {
        val xRanges = (xs sliceWith other.xs)
        val yRanges = (ys sliceWith other.ys)
        val zRanges = (zs sliceWith other.zs)

        for (xRange in xRanges) {
            for (yRange in yRanges) {
                for (zRange in zRanges) {
                    val cube = Cube(xRange, yRange, zRange)

                    if (!(cube intersects other)) {
                        add(cube)
                    }
                }
            }
        }
    }

    internal fun LongRange.size(): Long = (last - first + 1).takeUnless { isEmpty() } ?: 0L

    private infix fun LongRange.sliceWith(other: LongRange): Set<LongRange> = when {
        this in other -> sequenceOf(this)
        other in this -> sequenceOf(
            this.first until other.first,
            other,
            (other.last + 1)..this.last,
        )
        other.first in this -> sequenceOf(
            this.first until other.first,
            other.first..this.last
        )
        other.last in this -> sequenceOf(
            this.first..other.last,
            (other.last + 1)..this.last
        )
        else -> sequenceOf()
    }.filterNotTo(mutableSetOf()) { it.isEmpty() }

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

    private fun Instruction.isInitializationInstruction() = cube.xs in INITIALIZATION_RANGE &&
            cube.ys in INITIALIZATION_RANGE &&
            cube.zs in INITIALIZATION_RANGE

    private val INITIALIZATION_RANGE = -50L..50L

    private const val DIGIT = """([-\d]+)"""
    private const val RANGE = """$DIGIT..$DIGIT"""
}
