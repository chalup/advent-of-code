package org.chalup.utils

import java.io.File
import kotlin.system.measureTimeMillis

fun advent(inputsDir: String, year: Int, day: Int, part: Int, solution: (List<String>) -> Any) {
    val input = { File(File(inputsDir, year.toString()), "day$day.txt").readLines() }

    val result: Any
    val elapsed = measureTimeMillis {
        result = solution(input())
    }

    println("Day $day, part $part [${elapsed}ms]: $result")
}
