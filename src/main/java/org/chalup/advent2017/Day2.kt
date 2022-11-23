package org.chalup.advent2017

import org.chalup.utils.minMax
import org.chalup.utils.parseNumbers

object Day2 {
    fun task1(input: List<String>) = input
        .sumOf { row ->
            parseNumbers(row)
                .minMax()
                .let { (min, max) -> max - min }
        }

    fun task2(input: List<String>) = input
        .sumOf { row ->
            parseNumbers(row)
                .let {
                    it.flatMapIndexed { i1, n1 ->
                        it.mapIndexedNotNull { i2, n2 ->
                            (n1 to n2).takeIf { i1 != i2 }
                        }
                    }
                }
                .first { (a, b) -> a >= b && a % b == 0 }
                .let { (a, b) -> a / b }
        }
}