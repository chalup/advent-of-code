package org.chalup.advent2022

import java.util.LinkedList

object Day21 {
    fun task1(input: List<String>) = input
        .let(::parseMonkeyExpressions)
        .let { expressionsById -> evaluate("root", expressionsById) }

    fun task2(input: List<String>) = input
        .let(::parseMonkeyExpressions)
        .let { expressionsById ->
            val (lhsId, rhsId) = expressionsById.getValue("root").let { it as MonkeyExpr.BinaryExpression }

            val pathToHuman = findPathToHuman(expressionsById, lhsId, rhsId)

            val value = listOf(rhsId, lhsId).single { it != pathToHuman.first() }.let { evaluate(it, expressionsById) }

            pathToHuman.zipWithNext().fold(value) { v, (next, following) ->
                val expr = expressionsById.getValue(next) as MonkeyExpr.BinaryExpression

                val eval = listOf(expr.lhsId, expr.rhsId).single { it != following }.let { evaluate(it, expressionsById) }

                when (expr.operand) {
                    '+' -> v - eval
                    '-' -> if (following == expr.lhsId) v + eval else eval - v
                    '*' -> v / eval
                    '/' -> if (following == expr.lhsId) v * eval else eval / v
                    else -> throw IllegalArgumentException("Unsupported op ${expr.operand}")
                }
            }
        }

    private fun findPathToHuman(expressionsById: Map<String, MonkeyExpr>, vararg rootExpressions: String): List<String> {
        val queue = LinkedList<List<String>>().apply { rootExpressions.mapTo(this) { listOf(it) } }

        while (queue.isNotEmpty()) {
            val head = queue.remove()

            if (head.last() == "humn") return head

            val (lhs, rhs) = expressionsById.getValue(head.last()).let { it as? MonkeyExpr.BinaryExpression } ?: continue
            queue.addFirst(head + lhs)
            queue.addFirst(head + rhs)
        }
        throw IllegalArgumentException("Can't find 'humn' node!")
    }

    private fun evaluate(id: String, expressionsById: Map<String, MonkeyExpr>): Long =
        when (val expr = expressionsById.getValue(id)) {
            is MonkeyExpr.BinaryExpression -> {
                val lhs = evaluate(expr.lhsId, expressionsById)
                val rhs = evaluate(expr.rhsId, expressionsById)

                val function: (Long, Long) -> Long = when (expr.operand) {
                    '+' -> Long::plus
                    '-' -> Long::minus
                    '*' -> Long::times
                    '/' -> Long::div
                    else -> throw IllegalArgumentException("Unsupported op ${expr.operand}")
                }

                function(lhs, rhs)
            }

            is MonkeyExpr.Literal -> expr.value
        }

    private fun parseMonkeyExpressions(input: List<String>) = input
        .associate {
            val (monkeyId, expression) = it.split(": ")

            val expr = when (val literal = expression.toLongOrNull()) {
                null -> {
                    val (lhs, op, rhs) = expression.split(" ")
                    MonkeyExpr.BinaryExpression(lhs, rhs, op.single())
                }

                else -> MonkeyExpr.Literal(literal)
            }

            monkeyId to expr
        }
}

private sealed interface MonkeyExpr {
    data class Literal(val value: Long) : MonkeyExpr
    data class BinaryExpression(val lhsId: String, val rhsId: String, val operand: Char) : MonkeyExpr
}
