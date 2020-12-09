package org.chalup.advent2018

import java.lang.Math.abs
import java.util.LinkedList

object Day9 {
    fun calculateHighScore(players: Int, marbles: Long): Long {
        val scores: LinkedList<Long> = LinkedList<Long>().apply { repeat(players) { addFirst(0) } }
        val board: LinkedList<Long> = LinkedList<Long>().apply { add(0) }

        fun <T> LinkedList<T>.rotate(delta: Int) = repeat(abs(delta)) {
            if (delta > 0) {
                board.addLast(board.removeFirst())
            } else {
                board.addFirst(board.removeLast())
            }
        }

        for (marble in 1..marbles) {
            if (marble % 23 != 0L) {
                board.rotate(2)
                board.addFirst(marble)
                scores.addLast(scores.removeFirst())
            } else {
                board.rotate(-7)
                var activePlayerScore = scores.removeFirst()
                activePlayerScore += marble
                activePlayerScore += board.removeFirst()
                scores.addLast(activePlayerScore)
            }
        }

        return scores.maxOrNull()!!
    }
}