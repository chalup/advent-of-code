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
        advent(day = 15, part = 1, Day15::task1)
        return@measureTimeMillis
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
        advent(day = 7, part = 1, Day7::task1)
        advent(day = 7, part = 2, Day7::task2)
        advent(day = 8, part = 1, Day8::task1)
        advent(day = 8, part = 2, Day8::task2)
        advent(day = 9, part = 1, Day9.Task1::task1)
        advent(day = 9, part = 2, Day9.Task2::task2)
        advent(day = 10, part = 1, Day10::task1)
        advent(day = 10, part = 2, Day10::task2)
        advent(day = 11, part = 1, Day11::task1)
        advent(day = 13, part = 1, Day13::task1)
        advent(day = 14, part = 1, Day14::task1)
        advent(day = 14, part = 2, Day14::task2)
    }

    println("=== TOTAL [${elapsedTotal}ms] ===")
}
