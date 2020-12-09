package org.chalup.advent2018

object Day5 {
    private fun Char.willReactWith(char: Char) = equals(char, ignoreCase = true) && !equals(char, ignoreCase = false)

    fun reduce(polymer: String): String = polymer
        .fold(emptyList<Char>()) { acc, char ->
            if (acc.lastOrNull()?.willReactWith(char) == true) {
                acc.dropLast(1)
            } else {
                acc + char
            }
        }
        .joinToString(separator = "")

    fun minimalImprovedPolymerLength(polymer: String): Int =
        polymer
            .toUpperCase()
            .toSet()
            .map { element ->
                polymer
                    .filterNot { it.equals(element, ignoreCase = true) }
                    .let { reduce(it) }
                    .length
            }
            .minOrNull()!!
}