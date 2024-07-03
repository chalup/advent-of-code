package org.chalup.advent2018

import kotlin.system.measureTimeMillis
import org.chalup.utils.advent as aoc

fun main(args: Array<String>) {
    fun advent(day: Int, part: Int, solution: (List<String>) -> Any) = aoc(
        args[0],
        year = 2018,
        day, part, solution
    )

    val elapsedTotal = measureTimeMillis {
        advent(day = 22, part = 1, solution = Day22::task1)
        advent(day = 22, part = 2, solution = Day22::task2)
        advent(day = 24, part = 1, solution = Day24::task1)
        advent(day = 24, part = 2, solution = Day24::task2)
    }

    println("=== TOTAL [${elapsedTotal}ms] ===")
}
