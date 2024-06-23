package org.chalup.advent2015

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
        advent(day = 13, part = 1, solution = Day13::task1)
        advent(day = 13, part = 2, solution = Day13::task2)
        advent(day = 14, part = 1, solution = Day14::task1)
        advent(day = 14, part = 2, solution = Day14::task2)
        advent(day = 15, part = 1, solution = Day15::task1)
        advent(day = 15, part = 2, solution = Day15::task2)
        advent(day = 16, part = 1, solution = Day16::task1)
        advent(day = 16, part = 2, solution = Day16::task2)
        // ugly bruteforce solution that takes 5s per task
        // advent(day = 17, part = 1, solution = Day17::task1)
        // advent(day = 17, part = 2, solution = Day17::task2)
        advent(day = 18, part = 1, solution = Day18::task1)
        advent(day = 18, part = 2, solution = Day18::task2)
    }

    println("=== TOTAL [${elapsedTotal}ms] ===")
}
