package org.chalup.advent2020

import com.google.common.collect.Sets

object Day1 {
    fun task1(input: List<String>): Int {
        val numbers = input.mapTo(mutableSetOf()) { it.toInt() }

        return numbers
            .filter { (2020 - it) in numbers }
            .fold(1, Int::times)
    }

    fun task2(input: List<String>): Int {
        val numbers = input.mapTo(mutableSetOf()) { it.toInt() }

        return numbers
            .asSequence()
            .flatMap { a -> numbers.asSequence().filter { it != a }.filter { it + a < 2020 }.map { a to it } }
            .flatMap { (a, b) -> numbers.asSequence().filter { it != a && it != b }.map { Triple(a, b, it) } }
            .filter { (a, b, c) -> a + b + c == 2020 }
            .map { (a, b, c) -> a * b * c }
            .first()
    }
}