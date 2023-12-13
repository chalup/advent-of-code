package org.chalup.advent2023

import org.chalup.utils.textBlocks

object Day13 {
    fun task1(input: List<String>): Long = input
        .let(::textBlocks)
        .sumOf { pattern -> findReflectionsIds(pattern).single() }

    fun task2(input: List<String>): Long = input
        .let(::textBlocks)
        .sumOf { pattern ->
            val originalId = findReflectionsIds(pattern).single()

            pattern
                .asSequence()
                .flatMapIndexed { lineIndex: Int, line: String ->
                    line
                        .mapIndexed { charIndex, c ->
                            val flippedChar = when (c) {
                                '#' -> '.'
                                '.' -> '#'
                                else -> throw IllegalArgumentException()
                            }

                            pattern
                                .toMutableList()
                                .apply {
                                    set(
                                        lineIndex,
                                        line.take(charIndex) + flippedChar + line.substring(charIndex + 1)
                                    )
                                }
                        }
                }
                .flatMap { findReflectionsIds(it) }
                .filter { it != originalId }
                .first()
        }
}

private fun findReflectionsIds(pattern: List<String>): Sequence<Long> =
    sequence {
        yieldAll(findVerticalReflectionsIndices(pattern).map { it * 1L })
        yieldAll(findHorizontalReflectionsIndices(pattern).map { it * 100L })
    }

private fun findHorizontalReflectionsIndices(pattern: List<String>): Sequence<Int> =
    findReflectionIndices(pattern)

private fun findVerticalReflectionsIndices(pattern: List<String>): Sequence<Int> =
    pattern
        .first()
        .indices
        .map { index -> pattern.asSequence().map { it[index] }.joinToString(separator = "") }
        .let(::findReflectionIndices)

private fun findReflectionIndices(data: List<Any>): Sequence<Int> =
    data
        .let { 0 until data.lastIndex }
        .asSequence()
        .filter { index ->
            val reflectionSize = minOf(
                index + 1,
                data.lastIndex - index
            )

            (0 until reflectionSize)
                .all { di -> data[index - di] == data[index + 1 + di] }
        }
        .map { it + 1 }