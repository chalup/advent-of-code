package org.chalup.advent2022

import org.chalup.advent2018.cycle
import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.plus

object Day17 {
    fun task1(input: List<String>) = input
        .single()
        .let { jet ->
            val jetIterator = jet.toList().cycle().iterator()
            val shapesIterator = Shape.values().toList().cycle().iterator()

            val floorLevels = MutableList(7) { 0 }
            val bricks = mutableSetOf<Point>().apply {
                for (x in 0 until 7) add(Point(x, 0))
            }

            repeat(2022) {
                val shape = shapesIterator.next()
                var shapePosition = Point(2, floorLevels.max() + 4)

                fun canMove(vector: Vector): Boolean = shape.bricksVectors.all { v ->
                    when (val newBrickPosition = shapePosition + v + vector) {
                        in bricks -> false
                        else -> newBrickPosition.x in 0 until 7
                    }
                }

                while (true) {
                    // push the shape
                    val dx = when (val jet = jetIterator.next()) {
                        '>' -> +1
                        '<' -> -1
                        else -> throw IllegalArgumentException("Unrecognized jet char '$jet' received")
                    }
                    if (canMove(Vector(dx, 0))) {
                        shapePosition += Vector(dx, 0)
                    }

                    if (canMove(Vector(0, -1))) {
                        shapePosition += Vector(0, -1)
                    } else {
                        shape.bricksVectors
                            .asSequence()
                            .map { shapePosition + it }
                            .forEach {
                                floorLevels[it.x] = maxOf(floorLevels[it.x], it.y)
                                bricks += it
                            }
                        break
                    }
                }
            }

            floorLevels.max()
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

    val bricksVectors = shape
        .lines()
        .reversed()
        .flatMapIndexed { dy: Int, line: String ->
            line.mapIndexedNotNull { dx, char ->
                Vector(dx, dy).takeIf { char == '#' }
            }
        }
}