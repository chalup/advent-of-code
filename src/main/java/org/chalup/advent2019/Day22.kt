package org.chalup.advent2019

import kotlin.math.absoluteValue

object Day22 {
    fun task1(map: List<String>): Int {
        val initialDeck = List(10007) { it }

        val shuffledDeck = map.fold(initialDeck) { deck, instruction ->
            val i = instruction.split(" ")

            when {
                i[0] == "cut" -> {
                    val amount = i.last().toInt()

                    if (amount > 0) {
                        deck.drop(amount) + deck.take(amount)
                    } else {
                        deck.takeLast(amount.absoluteValue) + deck.dropLast(amount.absoluteValue)
                    }
                }
                i[0] == "deal" && i[1] == "with" -> {
                    val increment = i.last().toInt()

                    MutableList(deck.size) { 0 }.apply {
                        deck.forEachIndexed { index, i ->
                            this[index * increment % deck.size] = i
                        }
                    }
                }
                i[0] == "deal" && i[1] == "into" -> deck.reversed()
                else -> throw IllegalStateException("Don't know how to handle $instruction")
            }
        }

        return shuffledDeck.indexOf(2019)
    }
}