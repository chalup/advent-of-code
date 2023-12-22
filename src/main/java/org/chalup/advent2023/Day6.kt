package org.chalup.advent2023

import org.chalup.utils.size
import kotlin.math.sqrt

object Day6 {
    fun task1(input: List<String>): Long = input
        .let(::parseRacesData)
        .let { race ->
            race
                .map { (time, record) -> waysToWinTheRace(time, record) }
                .fold(1, Long::times)
        }

    fun task2(input: List<String>): Long = input
        .let(::parseSingleRaceData)
        .let { (time, record) -> waysToWinTheRace(time, record) }
}

private fun waysToWinTheRace(time: Long, record: Long): Long {
    // Travelled distance is a quadratic equation
    //
    // distance = prep * ( time - prep ) = -prep^2 + time * prep
    //
    // We can shift it down by the current record and calculate
    // the intersection of the function with the X axis of:
    //
    // -x^2 + time * x - record = 0

    val d = sqrt(time * time - 4.0 * record)

    val x1 = (-time - d) / -2
    val x2 = (-time + d) / -2

    return ((minOf(x1, x2).toLong().inc())..(maxOf(x1, x2).toLong())).size()
}

private fun parseRacesData(input: List<String>) = input
    .map { line ->
        line
            .substringAfter(":")
            .trim()
            .split(' ')
            .mapNotNull { value ->
                value.trim().takeIf { it.isNotEmpty() }
            }
            .map { it.toLong() }
    }
    .let { (times, distances) -> times.zip(distances) }

private fun parseSingleRaceData(input: List<String>) = input
    .map { line ->
        line
            .substringAfter(":")
            .replace(" ", "")
            .trim()
            .toLong()
    }
    .let { (time, distance) -> time to distance }