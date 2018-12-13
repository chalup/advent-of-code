package org.chalup.advent2015

object Day11 {
    fun String.letterPairs() = this
        .zipWithNext { a, b -> if (a == b) a else null }
        .filterNotNull()
        .toSet()

    fun String.hasStraight() = this
        .zipWithNext()
        .zipWithNext { (a, b), (_, c) -> (a + 1 == b && b + 1 == c) }
        .any { it }

    fun String.isGoodPassword() = when {
        contains('i') -> false
        contains('l') -> false
        contains('o') -> false
        letterPairs().size < 2 -> false
        else -> hasStraight()
    }

    fun String.next() = reversed()
        .fold("" to true) { (result, increment), char ->
            when {
                !increment -> (result + char) to increment
                char == 'z' -> (result + 'a') to increment
                else -> (result + (char + 1)) to false
            }
        }
        .let { (result, increment) ->
            if (increment) {
                result + 'a'
            } else {
                result
            }
        }
        .reversed()

    fun String.nextGoodPassword() = generateSequence(next()) { it.next() }
        .dropWhile { !it.isGoodPassword() }
        .first()
}