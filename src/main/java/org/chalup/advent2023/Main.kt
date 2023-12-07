package org.chalup.advent2023

import kotlin.system.measureTimeMillis
import org.chalup.utils.advent as aoc

fun main(args: Array<String>) {
    fun advent(day: Int, part: Int, solution: (List<String>) -> Any) = aoc(
        args[0],
        year = 2023,
        day, part, solution
    )

    val elapsedTotal = measureTimeMillis {
        advent(day = 1, part = 1, Day1::task1)
        advent(day = 1, part = 2, Day1::task2)
        advent(day = 2, part = 1, Day2::task1)
        advent(day = 2, part = 2, Day2::task2)
        advent(day = 3, part = 1, Day3::task1)
        advent(day = 3, part = 2, Day3::task2)
        advent(day = 4, part = 1, Day4::task1)
        advent(day = 4, part = 2, Day4::task2)
        advent(day = 5, part = 1, Day5::task1)
        advent(day = 5, part = 2, Day5::task2)
        advent(day = 6, part = 1, Day6::task1)
        advent(day = 6, part = 2, Day6::task2)
    }

    println("=== TOTAL [${elapsedTotal}ms] ===")
}
