package org.chalup.advent2024

import kotlin.math.pow

object Day7 {
    fun task1(input: List<String>): Long = parseExpressions(input)
        .asSequence()
        .filter { it.isPossible(listOf(Operator.ADDITION, Operator.MULTIPLICATION)) }
        .sumOf { it.result }

    fun task2(input: List<String>): Long = parseExpressions(input)
        .asSequence()
        .filter { it.isPossible(Operator.entries.toList()) }
        .sumOf { it.result }

    private fun parseExpressions(input: List<String>) = input.map { line ->
        line
            .split(":")
            .map { it.trim() }
            .let { (result, arguments) ->
                Expression(
                    result = result.toLong(),
                    arguments = arguments.split(" ").map { it.toLong() }
                )
            }
    }
}

private enum class Operator { ADDITION, MULTIPLICATION, CONCATENATION }

private data class Expression(
    val result: Long,
    val arguments: List<Long>,
) {
    // TODO: dynamic programming instead of brute force?
    fun isPossible(operators: List<Operator>): Boolean =
        (0 until (operators.size pow (arguments.size - 1)))
            .any { operatorsMask ->
                arguments
                    .fold(0L to operatorsMask * operators.size) { (acc, ops), argument ->
                        val op = operators[ops % operators.size]

                        val runningResult = when (op) {
                            Operator.ADDITION -> acc + argument
                            Operator.MULTIPLICATION -> acc * argument
                            Operator.CONCATENATION -> (acc.toString() + argument.toString()).toLong()
                        }

                        if (runningResult > result) return@any false

                        runningResult to (ops / operators.size)
                    }
                    .let { (actualResult, _) -> actualResult == result }
            }
}

infix fun Int.pow(exponent: Int) = this.toDouble().pow(exponent).toInt()