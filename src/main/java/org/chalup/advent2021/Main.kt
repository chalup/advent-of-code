package org.chalup.advent2021

import java.io.File
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    fun advent(day: Int, part: Int, testSet: String? = null, block: (() -> List<String>) -> Any) {
        lateinit var result: Any

        val input = { File(args[0], "day${day}${testSet?.let { "-$it" } ?: ""}.txt").readLines() }

        val elapsed = measureTimeMillis {
            result = block(input)
        }

        println("Day $day, part $part [${elapsed}ms]: $result")
    }

    val elapsedTotal = measureTimeMillis {
        advent(day = 1, part = 1) { input -> Day1.task1(input()) }
        advent(day = 1, part = 2) { input -> Day1.task2(input()) }
        advent(day = 2, part = 1) { input -> Day2.task1(input()) }
        advent(day = 2, part = 2) { input -> Day2.task2(input()) }
        advent(day = 3, part = 1) { input -> Day3.task1(input()) }
        advent(day = 3, part = 2) { input -> Day3.task2(input()) }
        advent(day = 4, part = 1) { input -> Day4.task1(input()) }
        advent(day = 4, part = 2) { input -> Day4.task2(input()) }
        advent(day = 5, part = 1) { input -> Day5.task1(input()) }
        advent(day = 5, part = 2) { input -> Day5.task2(input()) }
        advent(day = 6, part = 1) { input -> Day6.task1(input().single()) }
        advent(day = 6, part = 2) { input -> Day6.task2(input().single()) }
        advent(day = 7, part = 1) { input -> Day7.task1(input().single()) }
        advent(day = 7, part = 2) { input -> Day7.task2(input().single()) }
        advent(day = 8, part = 1) { input -> Day8.task1(input()) }
        advent(day = 8, part = 2) { input -> Day8.task2(input()) }
        advent(day = 9, part = 1) { input -> Day9.task1(input()) }
        advent(day = 9, part = 2) { input -> Day9.task2(input()) }
        advent(day = 10, part = 1) { input -> Day10.task1(input()) }
        advent(day = 10, part = 2) { input -> Day10.task2(input()) }
        advent(day = 11, part = 1) { input -> Day11.task1(input()) }
        advent(day = 11, part = 2) { input -> Day11.task2(input()) }
        advent(day = 12, part = 1) { input -> Day12.task1(input()) }
        advent(day = 12, part = 2) { input -> Day12.task2(input()) }
        advent(day = 13, part = 1) { input -> Day13.task1(input()) }
        advent(day = 13, part = 2) { input -> Day13.task2(input()) }
        advent(day = 14, part = 1) { input -> Day14.task1(input()) }
        advent(day = 14, part = 2) { input -> Day14.task2(input()) }
        advent(day = 15, part = 1) { input -> Day15.task1(input()) }
        advent(day = 15, part = 2) { input -> Day15.task2(input()) }
        advent(day = 16, part = 1) { input -> Day16.task1(input()) }
        advent(day = 16, part = 2) { input -> Day16.task2(input()) }
    }

    println("=== TOTAL [${elapsedTotal}ms] ===")
}
