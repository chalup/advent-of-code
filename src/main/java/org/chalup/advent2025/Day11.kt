package org.chalup.advent2025

object Day11 {
    fun task1(input: List<String>): Int {
        val graph = input.associate { line ->
            val src = line.substringBefore(":")
            val dsts = line
                .substringAfter(": ")
                .split(" ")
                .toSet()

            src to dsts
        }

        val routesToOut = mutableMapOf<String, Int>()
        fun calculateRoutes(node: String): Int = routesToOut.getOrPut(node) {
            if (node == "out") {
                1
            } else {
                graph.getValue(node)
                    .sumOf { calculateRoutes(it) }
            }
        }

        return calculateRoutes("you")
    }
}