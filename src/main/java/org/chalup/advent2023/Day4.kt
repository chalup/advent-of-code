package org.chalup.advent2023

import kotlin.math.pow

object Day4 {
    fun task1(input: List<String>): Long = input
        .map { parseScratchcard(it) }
        .sumOf { scratchcard ->
            scratchcard
                .wins
                .let {
                    if (it == 0) 0L
                    else 2.0.pow(it - 1).toLong()
                }
        }

    fun task2(input: List<String>): Int = input
        .map { parseScratchcard(it) }
        .let { scratchcards -> 
            buildMap<Scratchcard, Int> {  
                putAll(scratchcards.map { it to 1 })
                
                scratchcards.forEachIndexed { i, scratchcard ->
                    val multiplier = getValue(scratchcard)
                    if (scratchcard.wins > 0) {
                        for (wonCardIndex in (i+1)..(i+scratchcard.wins)) {
                            val wonScratchcard = scratchcards.getOrNull(wonCardIndex) ?: break
                            put(wonScratchcard, getValue(wonScratchcard) + multiplier)
                        }
                    }
                }
            }
        }
        .values
        .sum()
}

private fun parseScratchcard(input: String): Scratchcard {
    val winningNumbers = input
        .substringAfter(':')
        .substringBefore('|')
        .trim()
        .split(' ')
        .mapNotNull { it.trim().takeIf(String::isNotBlank) }
        .also { check(it.size == it.toSet().size) }
        .mapTo(mutableSetOf()) { it.toLong() }

    val cardNumbers = input
        .substringAfter('|')
        .trim()
        .split(' ')
        .mapNotNull { it.trim().takeIf(String::isNotBlank) }
        .also { check(it.size == it.toSet().size) }
        .mapTo(mutableSetOf()) { it.toLong() }

    return Scratchcard(winningNumbers, cardNumbers)
}

private data class Scratchcard(
    val winningNumbers: Set<Long>,
    val cardNumbers: Set<Long>
) {
    val wins = (cardNumbers intersect winningNumbers).size
}