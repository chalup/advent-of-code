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

    fun findShortestAndLongestRoute(routes: List<String>) = routes
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
                        .sumBy { (a, b) -> routes[a to b] ?: routes[b to a] ?: throw IllegalStateException("Can't find route between $a and $b") }
                }
                .let { it.min()!! to it.max()!! }
        }
}