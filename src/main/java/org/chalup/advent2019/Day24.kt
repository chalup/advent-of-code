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

                        cellState(char, infestedNeighbours)
                    }
            }
        }

        fun biodiversityRating(layout: List<String>): Long = layout
            .joinToString(separator = "")
            .reversed()
            .replace('#', '1')
            .replace('.', '0')
            .toLong(radix = 2)

        val visited = mutableSetOf<List<String>>()
        for (layout in layoutSequence) {
            if (!visited.add(layout)) biodiversityRating(layout)
        }
        throw IllegalStateException("Huh?")
    }

    fun task2(input: List<String>): Int {
        operator fun List<List<String>>.get(depth: Int, x: Int, y: Int) = this
            .getOrNull(depth)
            ?.getOrNull(y)
            ?.getOrNull(x)
            ?: '.'

        fun List<String>.numberOfBugs() = this
            .sumOf { line ->
                line.count { it == '#' }
            }

        val layoutSequence = generateSequence(listOf(input)) { levels ->
            val newLevels = List<List<String>>(levels.size + 2) { depth ->
                List(5) { y ->
                    buildString {
                        repeat(5) { x ->
                            if (x == 2 && y == 2) {
                                append('?')
                            } else {
                                append(
                                    cellState(
                                        previousState = levels[depth - 1, x, y],
                                        infestedNeighbours = Direction.entries.sumOf { direction ->
                                            val nx = x + direction.vector.dx
                                            val ny = y + direction.vector.dy

                                            fun singleTile(d: Int, x: Int, y: Int) =
                                                if (levels[d, x, y] == '#') 1 else 0

                                            fun row(d: Int, y: Int) = (0 until 5).sumOf { x -> singleTile(d, x, y) }
                                            fun col(d: Int, x: Int) = (0 until 5).sumOf { y -> singleTile(d, x, y) }

                                            when {
                                                nx < 0 -> singleTile(depth - 2, 1, 2)
                                                nx > 4 -> singleTile(depth - 2, 3, 2)
                                                ny < 0 -> singleTile(depth - 2, 2, 1)
                                                ny > 4 -> singleTile(depth - 2, 2, 3)
                                                nx == 2 && ny == 2 -> when (direction) {
                                                    Direction.U -> row(depth, 4)
                                                    Direction.R -> col(depth, 0)
                                                    Direction.D -> row(depth, 0)
                                                    Direction.L -> col(depth, 4)
                                                }

                                                else -> singleTile(depth - 1, nx, ny)
                                            }
                                        }
                                    )
                                )
                            }
                        }
                    }
                }
            }

            newLevels
                .dropWhile { it.numberOfBugs() == 0 }
                .dropLastWhile { it.numberOfBugs() == 0 }
        }

        return layoutSequence
            .drop(200)
            .first()
            .sumOf { level -> level.numberOfBugs() }
    }

    private fun cellState(previousState: Char, infestedNeighbours: Int) = when (previousState) {
        '#' -> if (infestedNeighbours == 1) "#" else "."
        '.' -> if (infestedNeighbours in 1..2) "#" else "."
        else -> throw IllegalStateException()
    }
}