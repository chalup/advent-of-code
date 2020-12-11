package org.chalup.advent2020

import java.util.*

object Day9 {
    fun task1(input: List<String>): Long = validatedSequence(input)
        .dropWhile { (_, valid) -> valid }
        .first()
        .let { (number, _) -> number }

    fun task2(input: List<String>): Long = task1(input)
        .let { invalidNumber ->
            input
                .asSequence()
                .map(String::toLong)
                .scan(emptyList<Long>() to 0L) { (numbers, sum), next ->
                    var newSum = sum

                    val list = LinkedList(numbers)
                        .apply {
                            add(next); newSum += next
                            while (newSum > invalidNumber) {
                                newSum -= removeFirst()
                            }
                        }

                    list to newSum
                }
                .first { (_, sum) -> sum == invalidNumber }
        }
        .let { (numbers, _) -> numbers.minOrNull()!! + numbers.maxOrNull()!! }

    private fun validatedSequence(input: List<String>): Sequence<Pair<Long, Boolean>> = input
        .map(String::toLong)
        .let { numbers ->
            val validation = numbers
                .asSequence()
                .windowed(26) { it.last() in it.dropLast(1).pairs().mapTo(mutableSetOf()) { (a, b) -> a + b } }
                .let { List(25) { true }.asSequence() + it }

            numbers.asSequence().zip(validation)
        }

    private fun <T> List<T>.pairs(): Sequence<Pair<T, T>> = this
        .asSequence()
        .flatMapIndexed { index, number ->
            asSequence().mapIndexedNotNull { otherIndex, otherNumber ->
                (number to otherNumber).takeIf { index != otherIndex }
            }
        }
}
