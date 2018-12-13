package org.chalup.advent2018

import org.chalup.utils.matchOrThrow

object Day12 {
    data class Rule(val neighborhood: List<Boolean>, val result: Boolean)

    fun gameOfPots(rulesList: List<Rule>, initialState: Set<Int>): Sequence<Set<Int>> {
        val ruleSize = rulesList.map { it.neighborhood.size }.toSet().single()
        check(ruleSize % 2 == 1)
        val neighborhoodRange = ruleSize / 2
        val rules = rulesList.map { it.neighborhood to it.result }.toMap()

        return generateSequence(initialState) { state ->
            mutableSetOf<Int>().apply {
                val min = (state.min() ?: 0) - neighborhoodRange
                val max = (state.max() ?: 0) + neighborhoodRange

                val stateAsList = (min - neighborhoodRange..max + neighborhoodRange).map { it in state }

                for (i in min..max) {
                    // i == min => 0
                    val potNeighborhood = stateAsList.subList(i - min, i - min + ruleSize)
                    if (rules[potNeighborhood] == true) {
                        add(i)
                    }
                }
            }
        }
    }

    fun calculateChecksum(generationNumber: Long, generations: Sequence<Set<Int>>): Long {
        fun Set<Int>.relative() = min()!!.let { min -> map { it - min } }.toSet()

        generations
            .zipWithNext()
            .forEachIndexed { index, (current, next) ->
                if (index.toLong() == generationNumber) return current.sum().toLong()

                if (current.relative() == next.relative()) {
                    val delta = (next.sum() - current.sum()).toLong()

                    return (generationNumber - index) * delta + current.sum()
                }

                if (index > 1_000_000) throw IllegalStateException("Couldn't determine the pattern after 1M generations")
            }

        throw IllegalStateException("For some reason the generations Sequence got terminated")
    }

    private val initialStateRegex = "initial state: (.*?)".toRegex()

    private fun parseInitialState(initialState: String): Set<Int> =
        initialStateRegex
            .matchOrThrow(initialState)
            .destructured
            .let { (pots) ->
                pots
                    .mapIndexed { index, char -> index.takeIf { char == '#' } }
                    .filterNotNull()
                    .toSet()
            }

    private val ruleRegexp = "(.*?) => (.)".toRegex()
    private fun parseRule(rule: String): Rule =
        ruleRegexp
            .matchOrThrow(rule)
            .destructured
            .let { (neighborhood, result) ->
                Rule(neighborhood.map { it == '#' },
                     result == "#")
            }

    fun parseInput(input: List<String>): Pair<List<Rule>, Set<Int>> =
        input.drop(2).map { parseRule(it) } to parseInitialState(input[0])
}