package org.chalup.advent2020

object Day6 {
    fun task1(input: List<String>): Int = parseAnswers(input)
        .sumOf { groupAnswers ->
            groupAnswers
                .flatMapTo(mutableSetOf()) { it.asIterable() }
                .size
        }

    fun task2(input: List<String>): Int = parseAnswers(input)
        .sumOf { groupAnswers ->
            groupAnswers
                .map { it.toSet() }
                .reduce { a, b -> a intersect b }
                .size
        }

    private fun parseAnswers(input: List<String>): Sequence<List<String>> = sequence {
        var answers = mutableListOf<String>()

        input.forEach { line ->
            if (line.isBlank()) {
                yield(answers)
                answers = mutableListOf()
            } else {
                answers.add(line)
            }
        }

        if (answers.isNotEmpty()) yield(answers)
    }
}