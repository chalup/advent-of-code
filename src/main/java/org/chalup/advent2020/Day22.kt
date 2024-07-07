package org.chalup.advent2020

import org.chalup.utils.textBlocks
import java.util.LinkedList

object Day22 {
    fun task1(input: List<String>): Int {
        val decks = textBlocks(input).map { it.drop(1).mapTo(LinkedList(), String::toInt) }

        while (decks.all { it.isNotEmpty() }) {
            val drawnCards = List(decks.size) { decks[it].removeFirst() }
            val winningCard = drawnCards.max()
            val winnerIndex = drawnCards.indexOf(winningCard)

            decks[winnerIndex].addAll(drawnCards.sortedDescending())
        }

        return decks
            .single { it.isNotEmpty() }
            .asReversed()
            .mapIndexed { i, card -> card * (i + 1) }
            .sum()
    }

    fun task2(input: List<String>): Int {
        val decks = textBlocks(input).map { it.drop(1).mapTo(LinkedList(), String::toInt) }

        fun playGame(decks: List<MutableList<Int>>): Int {
            val previousDecksArrangements: MutableSet<List<List<Int>>> = mutableSetOf()

            while (decks.all { it.isNotEmpty()} ) {
                if (!previousDecksArrangements.add(decks.map { it.toList() })) return 0

                val drawnCards = List(decks.size) { decks[it].removeFirst() }

                val winnerIndex = if (drawnCards.withIndex().all { (i, card) -> decks[i].size >= card }) {
                    playGame(drawnCards.withIndex().map { (i, card) -> decks[i].take(card).toMutableList() })
                } else {
                    val winningCard = drawnCards.max()
                    drawnCards.indexOf(winningCard)
                }

                decks[winnerIndex].add(drawnCards[winnerIndex])
                decks[winnerIndex].add(drawnCards[(winnerIndex + 1) % drawnCards.size])
            }

            return decks.indexOfFirst { it.isNotEmpty() }
        }

        return decks[playGame(decks)]
            .asReversed()
            .mapIndexed { i, card -> card * (i + 1) }
            .sum()
    }
}
