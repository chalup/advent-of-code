package org.chalup.advent2016

object Day15 {
    fun task1(input: List<String>): Int = solve(parseDisksSetup(input))
    fun task2(input: List<String>): Int = solve(parseDisksSetup(input) + (11 to 0))

    private fun solve(disks: List<Pair<Int, Int>>): Int {
        return generateSequence(0) { it + 1 }
            .first { t ->
                disks
                    .mapIndexed { diskIndex, (positions, initialPosition) ->
                        (initialPosition + t + (diskIndex + 1)) % positions
                    }
                    .all { it == 0 }
            }
    }

    private fun parseDisksSetup(input: List<String>): List<Pair<Int, Int>> = input.map {
        val numberOfPositions = it.substringAfter("has ").substringBefore(" positions").toInt()
        val initialPosition = it.substringAfter(" at position ").substringBefore(".").toInt()

        numberOfPositions to initialPosition
    }
}
