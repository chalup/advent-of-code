package org.chalup.advent2020

import java.util.LinkedList

object Day18 {
    fun task1(input: List<String>): Long = input
        .map { evaluate(it.replace(" ", "").iterator()) }
        .sum()
}

private fun evaluate(iterator: CharIterator): Long {
    fun nextValue(iterator: CharIterator): Long {
        check(iterator.hasNext())
        val c = iterator.next()

        return when {
            c == '(' -> evaluate(iterator)
            c.isDigit() -> c.digitToInt().toLong()
            else -> throw IllegalStateException("Next value: $c")
        }
    }

    val stack = LinkedList<Long>()
    stack.push(nextValue(iterator))

    while (iterator.hasNext()) {
        when (val c = iterator.next()) {
            ')' -> return stack.single()
            '+' -> stack.push(stack.pop() + nextValue(iterator))
            '*' -> stack.push(stack.pop() * nextValue(iterator))
            else -> throw IllegalStateException("Evaluate: $c")
        }
    }

    return stack.single()
}
