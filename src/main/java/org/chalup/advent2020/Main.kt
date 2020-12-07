package org.chalup.advent2020

import java.io.File
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    fun advent(day: Int, part: Int, block: (() -> List<String>) -> Any) {
        lateinit var result: Any

        val input = { File(args[0], "day${day}.txt").readLines() }

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
    }

    println("=== TOTAL [${elapsedTotal}ms] ===")
}
