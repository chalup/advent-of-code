package org.chalup.advent2022

object Day21 {
    fun task1(input: List<String>) = input
        .let(::parseMonkeyExpressions)
        .let { expressionsById -> evaluate("root", expressionsById) }

    private fun evaluate(id: String, expressionsById: Map<String, MonkeyExpr>): Long =
        when (val expr = expressionsById.getValue(id)) {
            is MonkeyExpr.BinaryExpression -> expr.op(evaluate(expr.lhsId, expressionsById), evaluate(expr.rhsId, expressionsById))
            is MonkeyExpr.Literal -> expr.value
        }

    private fun parseMonkeyExpressions(input: List<String>) = input
        .associate {
            val (monkeyId, expression) = it.split(": ")

            val expr = when (val literal = expression.toLongOrNull()) {
                null -> {
                    val (lhs, op, rhs) = expression.split(" ")
                    val function: (Long, Long) -> Long = when (op.single()) {
                        '+' -> Long::plus
                        '-' -> Long::minus
                        '*' -> Long::times
                        '/' -> Long::div
                        else -> throw IllegalArgumentException("Unsupported op $op")
                    }

                    MonkeyExpr.BinaryExpression(lhs, rhs, function)
                }

                else -> MonkeyExpr.Literal(literal)
            }

            monkeyId to expr
        }
}

private sealed interface MonkeyExpr {
    data class Literal(val value: Long) : MonkeyExpr
    class BinaryExpression(val lhsId: String, val rhsId: String, val op: (Long, Long) -> Long) : MonkeyExpr
}
