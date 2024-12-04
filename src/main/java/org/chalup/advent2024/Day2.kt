package org.chalup.advent2024

object Day2 {
    fun task1(input: List<String>): Int = input
        .asSequence()
        .map { line -> line.split(" ").map(String::toInt) }
        .count(::isValidReport)

    private fun isValidReport(report: List<Int>): Boolean {
        if (report.size <= 1) return true

        val allIncreasing by lazy { report.zipWithNext().all { (a, b) -> b > a && b - a <= 3 } }
        val allDecreasing by lazy { report.zipWithNext().all { (a, b) -> b < a && a - b <= 3 } }

        return allIncreasing || allDecreasing
    }

    fun task2(input: List<String>): Int = input
        .asSequence()
        .map { line -> line.split(" ").map(String::toInt) }
        .count { report ->
            report.indices.any { indexToRemove ->
                isValidReport(report.toMutableList().apply { removeAt(indexToRemove) })
            }
        }
}