package org.chalup.advent2022

object Day6 {
    fun task1(input: List<String>) = input
        .single()
        .let { findMarkerIndex(it, markerSize = 4) }

    fun task2(input: List<String>) = input
        .single()
        .let { findMarkerIndex(it, markerSize = 14) }

    private fun findMarkerIndex(data: String, markerSize: Int): Int {
        return data
            .windowedSequence(size = markerSize, step = 1, partialWindows = false)
            .indexOfFirst { it.toSet().size == markerSize }
            .plus(markerSize)
    }
}