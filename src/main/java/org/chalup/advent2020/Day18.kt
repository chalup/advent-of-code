package org.chalup.advent2020

import com.google.common.collect.Iterators
import org.chalup.advent2020.Expression.GroupedExpression
import org.chalup.advent2020.Expression.Literal

object Day18 {
    fun task1(input: List<String>): Long = calculateSum(input, mapOf('+' to 1, '*' to 1))
    fun task2(input: List<String>): Long = calculateSum(input, mapOf('+' to 2, '*' to 1))

    private fun calculateSum(input: List<String>, operatorPrecedenceTable: Map<Char, Int>): Long = input
        .asSequence()
        .map { it.replace(" ", "") }
        .map { Parser(it, operatorPrecedenceTable) }
        .map { it.parseExpression() }
        .map { evaluate(it) }
        .sum()
}

private fun evaluate(expression: Expression): Long =
    when (expression) {
        is GroupedExpression -> evaluate(expression.e)
        is Expression.InfixExpression -> {
            val l = evaluate(expression.lhs)
            val r = evaluate(expression.rhs)
            when (val operator = expression.operator) {
                '+' -> l + r
                '*' -> l * r
                else -> throw IllegalStateException("Unsupported operator $operator")
            }
        }

        is Literal -> expression.value.toLong()
    }

class Parser(
    input: String,
    private val precedenceLookup: Map<Char, Int>
) {
    private val iterator = Iterators.peekingIterator(input.iterator())

    fun peek(): Char = iterator.peek()
    fun next(): Char = iterator.next()
    fun hasNext(): Boolean = iterator.hasNext()
    fun precedence(char: Char) = precedenceLookup[char] ?: 0
}

private fun Parser.parseInfixExpression(lhs: Expression): Expression.InfixExpression {
    val operator = next()
    val rhs = parseExpression(precedence = precedence(operator))
    return Expression.InfixExpression(lhs, operator, rhs)
}

private fun Parser.parsePrefixExpression(): Expression {
    return when (val c = peek()) {
        in '0'..'9' -> Literal(next().digitToInt())
        '(' -> {
            check(next() == '(')
            val e = parseExpression(0)
            check(next() == ')')
            GroupedExpression(e)
        }

        else -> throw IllegalStateException("Can't parse prefix expression for token $c")
    }
}

private fun Parser.parseExpression(precedence: Int = 0): Expression {
    var left = parsePrefixExpression()
    while (hasNext() && precedence < precedence(peek())) {
        left = parseInfixExpression(left)
    }

    return left
}

private sealed interface Expression {
    data class Literal(val value: Int) : Expression
    data class GroupedExpression(val e: Expression) : Expression
    data class InfixExpression(
        val lhs: Expression,
        val operator: Char,
        val rhs: Expression
    ) : Expression
}