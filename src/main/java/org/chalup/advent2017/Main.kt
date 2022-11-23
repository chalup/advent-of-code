package org.chalup.advent2017

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
    }

    println("=== TOTAL [${elapsedTotal}ms] ===")
}
