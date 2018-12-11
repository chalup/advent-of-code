package org.chalup.advent2015

object Day10 {
    class Accumulator(var current: Char? = null,
                      var count: Int = 0,
                      val result: StringBuilder = StringBuilder()) {
        fun appendCurrent() {
            result.append(count)
            result.append(current!!)
        }

        fun swapChar(char: Char) {
            current = char
            count = 1
        }

        fun inc() {
            count++
        }
    }

    fun lookAndSay(text: String): String = text
        .also { println("Look and say at text of length ${text.length}") }
        .fold(Accumulator()) { acc, char ->
            acc.apply {
                when {
                    current == null -> swapChar(char)
                    current != char -> {
                        appendCurrent()
                        swapChar(char)
                    }
                    else -> inc()
                }
            }
        }
        .apply { appendCurrent() }
        .result
        .toString()

    fun repeatLookAndSay(startingText: String, repetitions: Int): String =
        generateSequence(startingText) { lookAndSay(it) }
            .take(repetitions + 1)
            .last()
}