package org.chalup.advent2018

import java.util.LinkedList

object Day9 {
    fun calculateHighScore(players: Int, marbles: Int): Int {
        val scores = MutableList(players) { 0 }

        var activePlayer = 0
        val board: MutableList<Int> = LinkedList<Int>().apply { add(0) }
        var currentMarblePosition = 0

        fun marblePosition(delta: Int) =
            (currentMarblePosition + delta + board.size) % board.size

        for (marble in 1..marbles) {
            if (marble % 10_000 == 0) println("Marble $marble out of $marbles")

            if (marble % 23 != 0) {
                val newMarblePosition = marblePosition(delta = 2)
                board.add(newMarblePosition, marble)
                currentMarblePosition = newMarblePosition
            } else {
                val removedMarblePosition = marblePosition(delta = -7)
                scores[activePlayer] += marble
                scores[activePlayer] += board.removeAt(removedMarblePosition)
                currentMarblePosition = removedMarblePosition
            }

            activePlayer = (activePlayer + 1) % players
        }

        return scores.max()!!
    }
}