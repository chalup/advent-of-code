package org.chalup.advent2016

object Day6 {
    fun task1(input: List<String>): String = decode(input) {
        it.maxByOrNull { (_, count) -> count }?.let { (char, _) -> char }!!
    }

    fun task2(input: List<String>): String = decode(input) {
        it.minByOrNull { (_, count) -> count }?.let { (char, _) -> char }!!
    }

    private fun decode(input: List<String>, decodeFunction: (Map<Char, Int>) -> Char): String = input
        .asSequence()
        .flatMap { line ->
            line
                .asSequence()
                .mapIndexed { index, c -> index to c }
        }
        .groupBy(
            keySelector = { (index, _) -> index },
            valueTransform = { (_, char) -> char },
        )
        .mapValues { (_, chars) ->
            chars
                .groupingBy { it }
                .eachCount()
                .let(decodeFunction)
        }
        .entries
        .sortedBy { (position, _) -> position }
        .map { (_, char) -> char }
        .joinToString(separator = "")
}