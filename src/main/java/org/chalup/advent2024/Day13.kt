package org.chalup.advent2024

import org.chalup.utils.Vector
import org.chalup.utils.parseNumbers
import org.chalup.utils.plus
import org.chalup.utils.textBlocks
import org.chalup.utils.times

object Day13 {
    fun task1(input: List<String>): Int {
        val claws = textBlocks(input).map { (a, b, prize) ->
            Claaaaw(
                a = parseNumbers(a).let { (x, y) -> Vector(x, y) },
                b = parseNumbers(b).let { (x, y) -> Vector(x, y) },
                prize = parseNumbers(prize).let { (x, y) -> Vector(x, y) }
            )
        }

        return claws
            .mapNotNull { (aVector, bVector, prize) ->
                (0..100)
                    .flatMap { b -> (0..100).map { a -> a to b } }
                    .filter { (aClicks, bClicks) ->
                        prize == aVector * aClicks + bVector * bClicks
                    }
                    .map { (aClicks, bClicks) -> aClicks * 3 + bClicks }
                    .minOrNull()
            }
            .sum()
    }

    data class Claaaaw(
        val a: Vector,
        val b: Vector,
        val prize: Vector,
    )
}