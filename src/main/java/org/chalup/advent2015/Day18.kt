package org.chalup.advent2015

object Day18 {
    fun task1(input: List<String>): Int = simulateAndCountLights(input)
    fun task2(input: List<String>): Int = simulateAndCountLights(input, stuckCorners = true)

    private fun simulateAndCountLights(
        map: List<String>,
        cycles: Int = 100,
        stuckCorners: Boolean = false
    ) = generateSequence(map) { simulate(it, stuckCorners) }
        .drop(cycles)
        .first()
        .sumOf { line -> line.count { it == '#' } }

    fun simulate(map: List<String>, stuckCorners: Boolean) = map.mapIndexed { y, line ->
        line
            .mapIndexed { x, c ->
                val isCorner = (y == map.indices.first || y == map.indices.last) &&
                        (x == line.indices.first || x == line.indices.last)

                val neighbours = (-1..1).sumOf { dy ->
                    (-1..1).count { dx ->
                        (dx != 0 || dy != 0) && map.getOrNull(y + dy)?.getOrNull(x + dx) == '#'
                    }
                }

                when {
                    isCorner && stuckCorners -> '#'
                    c == '#' && (neighbours in 2..3) -> '#'
                    c == '.' && neighbours == 3 -> '#'
                    else -> '.'
                }
            }
            .joinToString(separator = "")
    }
}
