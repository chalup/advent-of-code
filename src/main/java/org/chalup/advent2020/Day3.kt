package org.chalup.advent2020

object Day3 {
    fun task1(input: List<String>): Int = countTrees(input, right = 3, down = 1)

    fun task2(input: List<String>): Long = SLOPES
        .map { (right, down) -> countTrees(input, right, down) }
        .map { it.toLong() }
        .fold(1L, Long::times)

    private fun countTrees(map: List<String>, right: Int, down: Int): Int = map
        .mapIndexedNotNull { i, row -> row.takeIf { i % down == 0 } }
        .mapIndexed { i, row -> row[(i * right) % row.length] }
        .count { it == '#' }

    private val SLOPES = listOf(
        1 to 1,
        3 to 1,
        5 to 1,
        7 to 1,
        1 to 2
    )
}