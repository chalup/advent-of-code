package org.chalup.advent2020

import com.google.common.collect.Sets

object Day1 {
    fun task1(input: List<String>): Int = solve(input, 2)
    fun task2(input: List<String>): Int = solve(input, 3)

    @Suppress("UnstableApiUsage")
    private fun solve(input: List<String>, n: Int): Int = input
        .mapTo(mutableSetOf(), String::toInt)
        .let { Sets.combinations(it, n) }
        .asSequence()
        .filter { it.reduce(Int::plus) == 2020 }
        .map { it.reduce(Int::times) }
        .first()
}