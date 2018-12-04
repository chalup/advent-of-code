package org.chalup.advent2015

import org.chalup.advent2018.scan

object Day1 {
    private fun String.parseDirections() = map {
        when (it) {
            '(' -> +1
            ')' -> -1
            else -> throw UnsupportedOperationException("Unsupported directions $it")
        }
    }

    fun findFloor(directions: String) = directions
        .parseDirections()
        .sum()

    fun basementDirectionsIndex(directions: String) = directions
        .parseDirections()
        .scan(0 to 0) { (index, currentFloor), direction -> (index + 1) to (currentFloor + direction) }
        .dropWhile { (_, floor) -> floor >= 0 }
        .map { (index, _) -> index }
        .first()
}