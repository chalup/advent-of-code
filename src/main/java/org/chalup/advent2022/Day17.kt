package org.chalup.advent2022

import org.chalup.advent2018.cycle
import org.chalup.advent2018.lcm
import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.plus

object Day17 {
    fun task1(input: List<String>) = input
        .single()
        .let { jet -> simulate(jet, 2022) }
        .size

    fun task2(input: List<String>) = input
        .single()
        .let { jet ->
            val maxCycleLength = lcm(jet.length, Shape.values().size)

            val linesToDrop = simulate(jet, maxCycleLength).size
            val lines = simulate(jet, maxCycleLength * 3).drop(linesToDrop)

            fun List<Int>.countBricks() = sumOf { it.countOneBits() }
            val bricksInShapesCycle = Shape.values().sumOf { it.bricksLines.countBricks() }

            val cycleSize = generateSequence(1) { n -> if (n < lines.size) n + 1 else null }
                .mapNotNull { n ->
                    val possibleCycle = lines.subList(0, n)

                    possibleCycle.countBricks()
                        .takeIf { it % bricksInShapesCycle == 0 }
                        ?.takeIf { possibleCycle == lines.subList(n, n * 2) }
                        ?.let { it / bricksInShapesCycle * Shape.values().size }
                }
                .first()

            val leftOvers = 1_000_000_000_000 % cycleSize
            val leftOversHeight = simulate(jet, leftOvers.toInt()).size
            val cycleHeight =  simulate(jet, cycleSize + leftOvers.toInt()).size - leftOversHeight
            val fullCyclesCount = 1_000_000_000_000 / cycleSize

            fullCyclesCount * cycleHeight + leftOversHeight
        }

    private fun simulate(jet: String, turns: Int): List<Int> {
        val jetIterator = jet.toList().cycle().iterator()
        val shapesIterator = Shape.values().toList().cycle().iterator()

        val bricks = mutableListOf(127)

        repeat(turns) {
            val shape = shapesIterator.next()
            var shapePosition = Point(2, bricks.lastIndex + 4)

            fun canMove(vector: Vector): Boolean = when {
                shapePosition.x + vector.dx < 0 -> false
                shapePosition.x + vector.dx + shape.width > 7 -> false
                else -> shape.bricksLines
                    .asSequence()
                    .mapIndexed { dy, mask ->
                        (bricks.getOrNull(shapePosition.y + dy + vector.dy) ?: 0) and (mask shl (shapePosition.x + vector.dx))
                    }
                    .all { it == 0 }
            }

            while (true) {
                // push the shape
                val dx = when (val direction = jetIterator.next()) {
                    '>' -> +1
                    '<' -> -1
                    else -> throw IllegalArgumentException("Unrecognized jet char '$direction' received")
                }
                if (canMove(Vector(dx, 0))) {
                    shapePosition += Vector(dx, 0)
                }

                if (canMove(Vector(0, -1))) {
                    shapePosition += Vector(0, -1)
                } else {
                    shape.bricksLines
                        .forEachIndexed { index, line ->
                            val lineY = index + shapePosition.y
                            val shapeMask = line shl shapePosition.x

                            if (lineY > bricks.lastIndex) {
                                bricks.add(shapeMask)
                            } else {
                                bricks[lineY] = bricks[lineY] or shapeMask
                            }
                        }

                    break
                }
            }
        }

        bricks.removeAt(0)
        return bricks
    }
}

private enum class Shape(shape: String) {
    DASH("####"),
    PLUS(
        """
        .#.
        ###
        .#.
        """.trimIndent()
    ),
    L(
        """
        ..#
        ..#
        ###
        """.trimIndent()
    ),
    I(
        """
        #
        #
        #
        #
        """.trimIndent()
    ),
    DOT(
        """
        ##
        ##
        """.trimIndent()
    );

    val width = shape.lines().maxOf { it.length }
    val bricksLines = shape
        .lines()
        .reversed()
        .map { line: String -> line.reversed().fold(0) { acc, char -> acc * 2 + (if (char == '#') 1 else 0) } }
}