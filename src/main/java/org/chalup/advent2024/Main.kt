package org.chalup.advent2024

import kotlin.system.measureTimeMillis
import org.chalup.utils.advent as aoc

fun main(args: Array<String>) {
    fun advent(day: Int, part: Int, solution: (List<String>) -> Any) = aoc(
        args[0],
        year = 2024,
        day, part, solution
    )

    val elapsedTotal = measureTimeMillis {
        advent(day = 1, part = 1, Day1::task1)
        advent(day = 1, part = 2, Day1::task2)
    }

    println("=== TOTAL [${elapsedTotal}ms] ===")
}
