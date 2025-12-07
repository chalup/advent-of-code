package org.chalup.advent2025

import org.chalup.utils.textBlocks

object Day6 {
    fun task1(input: List<String>): Long {
        val dataSets = input
            .dropLast(1)
            .map {
                it
                    .split("\\s+".toRegex())
                    .filter(String::isNotBlank)
                    .map(String::toLong)
            }

        return input
            .last()
            .split("\\s+".toRegex())
            .filter(String::isNotBlank)
            .mapIndexed { i, operator ->
                val function: (Long, Long) -> Long = when (operator) {
                    "+" -> Long::plus
                    "*" -> Long::times
                    else -> throw IllegalArgumentException("Unknown operator")
                }

                dataSets
                    .map { it[i] }
                    .reduce(function)
            }
            .sum()
    }

    fun task2(input: List<String>): Long {
        val columns = input.first().length
        val rows = input.size - 1

        val dataSets = List(columns) { column ->
            input
                .asSequence()
                .take(rows)
                .joinToString(separator = "") { it[column].toString() }
                .trim()
        }.let { textBlocks(it) }

        val operators: List<(Long, Long) -> Long> = input
            .last()
            .split("\\s+".toRegex())
            .filter(String::isNotBlank)
            .map { operator ->
                when (operator) {
                    "+" -> Long::plus
                    "*" -> Long::times
                    else -> throw IllegalArgumentException("Unknown operator")
                }
            }

        return dataSets
            .zip(operators) { data, operation ->
                data.map { it.toLong() }.reduce(operation)
            }
            .sum()
    }
}