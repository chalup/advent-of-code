package org.chalup.advent2023

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.bounds
import org.chalup.utils.contains
import org.chalup.utils.plus
import java.util.LinkedList

object Day18 {
    fun task1(input: List<String>): Int {
        data class DiggerInstruction(
            val direction: Direction,
            val length: Int,
            val color: String
        )

        fun parseInstruction(input: String) =
            DiggerInstruction(
                direction = Direction.fromSymbol(input.substringBefore(' ').trim()),
                length = input.substringAfter(' ').substringBefore('(').trim().toInt(),
                color = input.substringAfter('#').substringBefore(')')
            )

        val trench = buildSet {
            var diggerLocation = Point(0, 0)

            for (instruction in input.map(::parseInstruction)) {
                repeat(instruction.length) {
                    diggerLocation += instruction.direction.vector
                    add(diggerLocation)
                }
            }
        }

        val bounds = trench.bounds().let {
            Rect(
                it.topLeft + Direction.U.vector + Direction.L.vector,
                it.bottomRight + Direction.D.vector + Direction.R.vector,
            )
        }

        val heads = LinkedList<Point>().apply { add(bounds.topLeft) }
        val outside = mutableSetOf<Point>()
        while (heads.isNotEmpty()) {
            val head = heads.poll()

            if (!outside.add(head)) continue

            Direction
                .values()
                .mapNotNullTo(heads) { direction ->
                    (head + direction.vector)
                        .takeIf { it in bounds }
                        .takeUnless { it in trench }
                }
        }

        return (bounds.width * bounds.height) - outside.size
    }
}