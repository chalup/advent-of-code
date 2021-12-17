package org.chalup.advent2015

import com.google.common.collect.Collections2
import org.chalup.utils.matchOrThrow

object Day9 {
    private val regex = """(.*?) to (.*?) = (.*?)""".toRegex()

    private fun parseRoute(route: String): Pair<Pair<String, String>, Int> =
        regex
            .matchOrThrow(route)
            .destructured
            .let { (start, destination, distance) ->
                (start to destination) to distance.toInt()
            }

    @Suppress("UnstableApiUsage")
    fun findShortestAndLongestRoute(input: List<String>) = input
        .map { parseRoute(it) }
        .toMap()
        .let { routes ->
            val places = routes
                .keys
                .flatMap { (a, b) -> listOf(a, b) }
                .toSet()

            Collections2
                .permutations(places)
                .map {
                    it
                        .zipWithNext()
                        .sumOf { (a, b) -> routes[a to b] ?: routes[b to a] ?: throw IllegalStateException("Can't find route between $a and $b") }
                }
                .let { it.minOrNull()!! to it.maxOrNull()!! }
        }
}