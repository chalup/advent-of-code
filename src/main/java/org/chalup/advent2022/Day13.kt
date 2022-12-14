package org.chalup.advent2022

import org.chalup.utils.textBlocks

object Day13 {
    fun task1(input: List<String>) = input
        .let(::textBlocks)
        .asSequence()
        .map { it.map(::parseTerm) }
        .mapIndexedNotNull { i, (lhs, rhs) -> (i + 1).takeIf { lhs < rhs } }
        .sum()

    fun task2(input: List<String>) = input
        .let(::textBlocks)
        .asSequence()
        .flatMap { it }
        .map(::parseTerm)
        .toList()
        .let { terms ->
            val dividers = listOf(2, 6).map { parseTerm("[[$it]]") }

            val sortedTerms = (terms + dividers).sorted()

            dividers.map { sortedTerms.indexOf(it) + 1 }
        }
        .reduce(Int::times)
}


private fun parseTerm(input: String): Term {
    var pos = 0

    fun doParse(): Term =
        when (val nextChar = input[pos]) {
            in '0'..'9' -> {
                var result = 0

                do {
                    result *= 10
                    result += input[pos] - '0'
                } while (input[++pos].isDigit())

                Term.Number(result)
            }

            '[' -> {
                val terms = mutableListOf<Term>()

                pos++ // consume [
                while (input[pos] != ']') {
                    if (input[pos] == ',') pos++
                    terms += doParse()
                }
                pos++ // consume closing bracket

                Term.Array(terms)
            }

            else -> throw IllegalArgumentException("Encountered $nextChar, don't know what to do")
        }

    return doParse().also { term ->
        check(input == term.toString())
    }
}

private sealed interface Term : Comparable<Term> {
    @JvmInline
    value class Number(val value: Int) : Term {
        override fun toString(): String = value.toString()
    }

    data class Array(val terms: List<Term>) : Term {
        override fun toString(): String = terms.joinToString(separator = ",", prefix = "[", postfix = "]")
    }

    private fun toArray(): Array = when (this) {
        is Array -> this
        is Number -> Array(listOf(this))
    }

    override fun compareTo(other: Term): Int {
        when {
            this is Number && other is Number -> return when {
                this.value < other.value -> -1
                this.value > other.value -> +1
                else -> 0
            }

            this is Array && other is Array -> {
                val leftIterator = this.terms.iterator()
                val rightIterator = other.terms.iterator()

                while (leftIterator.hasNext() && rightIterator.hasNext()) {
                    val l = leftIterator.next()
                    val r = rightIterator.next()

                    when (val x = l.compareTo(r)) {
                        0 -> continue
                        else -> return x
                    }
                }

                return when {
                    rightIterator.hasNext() -> -1
                    leftIterator.hasNext() -> +1
                    else -> 0
                }
            }

            else -> return this.toArray().compareTo(other.toArray())
        }
    }
}