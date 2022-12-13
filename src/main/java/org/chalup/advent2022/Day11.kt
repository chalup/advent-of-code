package org.chalup.advent2022

import org.chalup.utils.matchRegex
import org.chalup.utils.textBlocks

object Day11 {
    fun task1(input: List<String>) = calculateMonkeyBusinessValue(input, numberOfRounds = 20) { _ -> { it / 3 } }
    fun task2(input: List<String>) = calculateMonkeyBusinessValue(input, numberOfRounds = 10_000) { monkeys ->
        val modulo = monkeys.map { it.spec.testFactor }.reduce(Long::times);

        { it % modulo }
    }
}

private fun calculateMonkeyBusinessValue(
    input: List<String>,
    numberOfRounds: Int,
    worryLevelFunctionBuilder: (Collection<Monkey>) -> ((Long) -> Long)
): Long {
    return parseMonkeys(input)
        .let { monkeysById ->
            val worryLevelFunction = worryLevelFunctionBuilder(monkeysById.values)
            Counter().apply {
                repeat(numberOfRounds) { simulateMonkeys(monkeysById, worryLevelFunction, this) }
            }
        }
        .values
        .sortedDescending()
        .take(2)
        .reduce(Long::times)
}

private data class Counter(private val mutableMap: MutableMap<String, Long> = mutableMapOf()) : Map<String, Long> by mutableMap {
    operator fun plusAssign(key: String) {
        mutableMap[key] = mutableMap.getOrDefault(key, 0) + 1
    }
}

private fun parseMonkeys(input: List<String>): Map<String, Monkey> = input
    .let(::textBlocks)
    .associate { (headerLine, itemsLine, operationLine, testLine, ifTrueLine, ifFalseLine) ->
        val monkeyId = headerLine.matchRegex("""Monkey (\d+):""") { (monkeyId) -> monkeyId }
        val items = itemsLine.substringAfter(":").split(",").mapTo(mutableListOf()) { it.trim().toLong() }

        val operation = operationLine.substringAfter("=").let {
            val (lhs, op, rhs) = it.trim().split(" ").map(String::trim)

            fun termFunc(term: String): (Long) -> Long = when (term) {
                "old" -> { old: Long -> old }
                else -> { _: Long -> term.toLong() }
            }

            { old: Long ->
                val l = termFunc(lhs).invoke(old)
                val r = termFunc(rhs).invoke(old)
                when (op) {
                    "+" -> l + r
                    "*" -> l * r
                    else -> throw IllegalArgumentException("Unexpected operation $op")
                }
            }
        }
        val testFactor = testLine.substringAfter("divisible by").trim().toLong()

        val successfulTestTarget = ifTrueLine.substringAfterLast(' ').trim()
        val failedTestTarget = ifFalseLine.substringAfterLast(' ').trim()

        monkeyId to Monkey(items, MonkeySpec(operation, testFactor, successfulTestTarget, failedTestTarget))
    }

private fun simulateMonkeys(monkeysById: Map<String, Monkey>, worryLevelFunction: (Long) -> Long, inspectionCounter: Counter) {
    for (monkeyId in monkeysById.keys.sorted()) {
        with(monkeysById.getValue(monkeyId)) {
            while (items.isNotEmpty()) {
                val item = items.removeFirst()
                val newItemLevel = spec.operation(item).let(worryLevelFunction)

                inspectionCounter += monkeyId

                val recipient = if (newItemLevel % spec.testFactor == 0L) spec.throwIfTrue else spec.throwIfFalse
                monkeysById.getValue(recipient).items += newItemLevel
            }
        }
    }
}

private operator fun <E> List<E>.component6() = get(5)

private data class Monkey(
    val items: MutableList<Long>,
    val spec: MonkeySpec
)

private data class MonkeySpec(
    val operation: (Long) -> Long,
    val testFactor: Long,
    val throwIfTrue: String,
    val throwIfFalse: String,
)