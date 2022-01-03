package org.chalup.advent2016

import java.io.EOFException

object Day9 {
    fun task1(input: List<String>): Long = input
        .single()
        .let(this::decompressedTextLength)

    fun task2(input: List<String>): Long = input
        .single()
        .let { decompressedTextLength(it, v2 = true) }

    private fun decompressedTextLength(text: String, v2: Boolean = false): Long {
        var decompressedLength = 0L
        val iterator = text.iterator()

        fun readNumber(until: Char): Int {
            var result = 0

            while (iterator.hasNext()) {
                val char = iterator.next()
                when {
                    char.isDigit() -> result = result * 10 + char.digitToInt()
                    char == until -> return result
                    else -> throw IllegalStateException("Unexpected character: $char")
                }
            }

            throw EOFException("Unexpected end of data while reading number delimited by $until")
        }

        while (iterator.hasNext()) {
            when (iterator.next()) {
                '(' -> {
                    val chunkSize = readNumber(until = 'x')
                    val repetitions = readNumber(until = ')')

                    val chunk = buildString {
                        repeat(chunkSize) { append(iterator.next()) }
                    }

                    decompressedLength += repetitions * (if (v2) decompressedTextLength(chunk, v2) else chunk.length.toLong())
                }
                else -> decompressedLength += 1
            }
        }

        return decompressedLength
    }
}