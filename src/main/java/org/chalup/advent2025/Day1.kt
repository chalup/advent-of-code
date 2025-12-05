package org.chalup.advent2025

import kotlin.math.absoluteValue

object Day1 {
    fun task1(input: List<String>): Int {
        return input
            .map {
                val rotation = it.drop(1).toInt() % 100

                when (val direction = it[0]) {
                    'L' -> 100 - rotation
                    'R' -> rotation
                    else -> throw IllegalArgumentException("Unexpected direction: $direction")
                }
            }
            .scan(50) { dial, n -> (dial + n) % 100 }
            .count { it == 0 }
    }

    fun task2(input: List<String>): Int {
        return input
            .map {
                val rotation = it.drop(1).toInt()

                val sign = when (val direction = it[0]) {
                    'L' -> -1
                    'R' -> +1
                    else -> throw IllegalArgumentException("Unexpected direction: $direction")
                }

                rotation * sign
            }
            .fold(50 to 0) { (dial, result), n ->
                val rotatedDial = dial + n

                val newDialPosition = when {
                    rotatedDial >= 0 -> rotatedDial % 100
                    else -> (100 - rotatedDial.absoluteValue % 100) % 100
                }

                val encounteredZeros = when {
                    rotatedDial > 0 -> rotatedDial / 100
                    rotatedDial < 0 -> (rotatedDial.absoluteValue / 100) + if (dial > 0) 1 else 0
                    else -> 1
                }

                newDialPosition to result + encounteredZeros
            }
            .let { (_, result) -> result }
    }
}