package org.chalup.advent2021

import org.chalup.advent2021.Day10.Issue.CorruptedLine
import org.chalup.advent2021.Day10.Issue.IncompleteLine
import java.util.LinkedList

object Day10 {
    fun task1(input: List<String>): Int = input
        .asSequence()
        .map(this::diagnoseLine)
        .filterIsInstance<CorruptedLine>()
        .map(CorruptedLine::corruptedChar)
        .sumOf(this::syntaxErrorScore)

    fun task2(input: List<String>): Long = input
        .asSequence()
        .map(this::diagnoseLine)
        .filterIsInstance<IncompleteLine>()
        .map(IncompleteLine::unclosedTokens)
        .map(this::autocompleteScore)
        .sorted()
        .toList()
        .let { it[it.size / 2] }

    private fun syntaxErrorScore(char: Char): Int = when (char) {
        ')' -> 3
        ']' -> 57
        '}' -> 1197
        '>' -> 25137
        else -> throw IllegalArgumentException("Unexpected char $char")
    }

    private fun autocompleteScore(unclosedTokens: List<Char>): Long = unclosedTokens
        .fold(0L) { acc, char ->
            val charScore = when (char) {
                '(' -> 1
                '[' -> 2
                '{' -> 3
                '<' -> 4
                else -> throw IllegalArgumentException("Unexpected char $char")
            }

            acc * 5 + charScore
        }

    private fun diagnoseLine(line: String): Issue {
        val stack = LinkedList<Char>()

        line.forEach {
            when (it) {
                '(', '[', '{', '<' -> stack.push(it)
                ')' -> if (stack.pop() != '(') return CorruptedLine(it)
                ']' -> if (stack.pop() != '[') return CorruptedLine(it)
                '}' -> if (stack.pop() != '{') return CorruptedLine(it)
                '>' -> if (stack.pop() != '<') return CorruptedLine(it)
                else -> return CorruptedLine(it)
            }
        }

        return IncompleteLine(unclosedTokens = stack)
    }

    private sealed class Issue {
        data class CorruptedLine(val corruptedChar: Char) : Issue()
        data class IncompleteLine(val unclosedTokens: List<Char>) : Issue()
    }
}
