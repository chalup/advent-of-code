package org.chalup.advent2019

import org.chalup.utils.Direction

object Day24 {
    fun task1(input: List<String>): Long {
        val layoutSequence = generateSequence(input) { map ->
            map.mapIndexed { y, line ->
                line
                    .withIndex()
                    .joinToString(separator = "") { (x, char) ->
                        val infestedNeighbours = Direction.entries.count { d ->
                            map
                                .getOrNull(y + d.vector.dy)
                                ?.getOrNull(x + d.vector.dx) == '#'
                        }

                        when (char) {
                            '#' -> if (infestedNeighbours == 1) "#" else "."
                            '.' -> if (infestedNeighbours in 1..2) "#" else "."
                            else -> throw IllegalStateException()
                        }
                    }
            }
        }

        val visited = mutableSetOf<List<String>>()
        for (layout in layoutSequence) {
            if (!visited.add(layout)) biodiversityRating(layout)
        }
        throw IllegalStateException("Huh?")
    }

    private fun biodiversityRating(layout: List<String>): Long = layout
        .joinToString(separator = "")
        .reversed()
        .replace('#', '1')
        .replace('.', '0')
        .toLong(radix = 2)
}