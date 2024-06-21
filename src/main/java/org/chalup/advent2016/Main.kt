package org.chalup.advent2016

import java.io.File
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    fun advent(day: Int, part: Int, testSet: String? = null, solution: (List<String>) -> Any) {
        lateinit var result: Any

        val input = { File(args[0], "day${day}${testSet?.let { "-$it" } ?: ""}.txt").readLines() }

        val elapsed = measureTimeMillis {
            result = solution(input())
        }

        println("Day $day, part $part [${elapsed}ms]: $result")
    }

    val elapsedTotal = measureTimeMillis {
        advent(day = 1, part = 1, solution = Day1::task1)
        advent(day = 1, part = 2, solution = Day1::task2)
        advent(day = 2, part = 1, solution = Day2::task1)
        advent(day = 2, part = 2, solution = Day2::task2)
        advent(day = 3, part = 1, solution = Day3::task1)
        advent(day = 3, part = 2, solution = Day3::task2)
        advent(day = 4, part = 1, solution = Day4::task1)
        advent(day = 4, part = 2, solution = Day4::task2)
        // these two take forever to process, don't run them every time
        // advent(day = 5, part = 1, solution = Day5::task1)
        // advent(day = 5, part = 2, solution = Day5::task2)
        advent(day = 6, part = 1, solution = Day6::task1)
        advent(day = 6, part = 2, solution = Day6::task2)
        advent(day = 7, part = 1, solution = Day7::task1)
        advent(day = 7, part = 2, solution = Day7::task2)
        advent(day = 8, part = 1, solution = Day8::task1)
        advent(day = 8, part = 2, solution = Day8::task2)
        advent(day = 9, part = 1, solution = Day9::task1)
        advent(day = 9, part = 2, solution = Day9::task2)
        advent(day = 10, part = 1, solution = Day10::task1)
        advent(day = 10, part = 2, solution = Day10::task2)
        // This takes forever as well
        // advent(day = 11, part = 1, solution = Day11::task1)
        // advent(day = 11, part = 2, solution = Day11::task2)
        advent(day = 12, part = 1, solution = Day12::task1)
        advent(day = 12, part = 2, solution = Day12::task2)
        advent(day = 13, part = 1, solution = Day13::task1)
        advent(day = 13, part = 2, solution = Day13::task2)
        // Stoopid MD5 puzzles
        // advent(day = 14, part = 1, solution = Day14::task1)
        // advent(day = 14, part = 2, solution = Day14::task2)
        advent(day = 15, part = 1, solution = Day15::task1)
        advent(day = 15, part = 2, solution = Day15::task2)
        advent(day = 16, part = 1, solution = Day16::task1)
        advent(day = 16, part = 2, solution = Day16::task2)
        advent(day = 17, part = 1, solution = Day17::task1)
        advent(day = 17, part = 2, solution = Day17::task2)
        advent(day = 18, part = 1, solution = Day18::task1)
        advent(day = 18, part = 2, solution = Day18::task2)
        advent(day = 19, part = 1, solution = Day19::task1)
        // TODO: advent(day = 19, part = 2, solution = Day19::task2)
        advent(day = 20, part = 1, solution = Day20::task1)
        advent(day = 20, part = 2, solution = Day20::task2)
        advent(day = 21, part = 1, solution = Day21::task1)
    }

    println("=== TOTAL [${elapsedTotal}ms] ===")
}
