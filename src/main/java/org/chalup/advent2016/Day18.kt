package org.chalup.advent2016

object Day18 {
    fun task1(input: List<String>): Int = countSafeTiles(input.first(), rowsCount = 40)
    fun task2(input: List<String>): Int = countSafeTiles(input.first(), rowsCount = 400_000)

    private fun countSafeTiles(initialRow: String, rowsCount: Int): Int {
        val trapPatters = setOf(
            "^^.",
            ".^^",
            "^..",
            "..^",
        )

        val rows = generateSequence(initialRow) { previousRow ->
            ".$previousRow."
                .windowed(size = 3, step = 1)
                .joinToString(separator = "") { if (it in trapPatters) "^" else "." }
        }

        return rows.take(rowsCount).sumOf { row -> row.count { it == '.' } }
    }
}
