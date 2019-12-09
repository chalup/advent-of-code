package org.chalup.advent2019

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
        advent(day = 2, part = 1) { input -> Day2.task1(input().first()) }
        advent(day = 2, part = 2) { input -> Day2.task2(input().first(), expectedOutput = 19690720) }
        advent(day = 3, part = 1) { input -> Day3.distanceToNearestIntersection(input()) }
        advent(day = 3, part = 2) { input -> Day3.shortestPath(input()) }
        advent(day = 4, part = 1) { Day4.task1(272091..815432) }
        advent(day = 4, part = 2) { Day4.task2(272091..815432) }
        advent(day = 5, part = 1) { input -> Day5.task1(input().first()) }
        advent(day = 5, part = 2) { input -> Day5.task2(input().first()) }
        advent(day = 6, part = 1) { input -> Day6.task1(input()) }
        advent(day = 6, part = 2) { input -> Day6.task2(input()) }
        advent(day = 7, part = 1) { input -> Day7.calculateMaxThrusterInput(input().first()) }
        advent(day = 7, part = 2) { input -> Day7.calculateMaxThrusterInputWithFeedbackLoop(input().first()) }
        advent(day = 8, part = 1) { input -> Day8.task1(input().first(), width = 25, height = 6) }
        advent(day = 8, part = 2) { input -> Day8.task2(input().first(), width = 25, height = 6) }
        advent(day = 9, part = 1) { input -> Day9.testBoostProgram(input().first()) }
        advent(day = 9, part = 2) { input -> Day9.runBoostProgram(input().first()) }
    }

    println("=== TOTAL [${elapsedTotal}ms] ===")
}
