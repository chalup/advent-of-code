package org.chalup.advent2019

import kotlin.math.absoluteValue

object Day16 {
    fun task1(input: List<String>): String = fft(
        initialSignal = input.single().map { it.digitToInt() },
        phases = 100
    )

    fun task2(input: List<String>): String {
        val resultIndex = input.single().take(7).toInt()
        val signal = input.single().map(Char::digitToInt)

        check(resultIndex * 2 > signal.size * 10_000)

        val repeatedSignal = buildList {
            repeat(10_000) { addAll(signal) }
        }.drop(resultIndex).reversed()

        val fftSequence = generateSequence(repeatedSignal) { prev ->
            prev.runningReduce { acc, digit -> (acc + digit) % 10 }
        }

        return fftSequence
            .drop(100)
            .first()
            .reversed()
            .take(8)
            .joinToString(separator = "")
    }

    private fun fft(initialSignal: List<Int>, phases: Int): String {
        fun fftPattern(index: Int) = sequence<Int> {
            val repeats = index + 1
            while (true) {
                repeat(repeats) { yield(0) }
                repeat(repeats) { yield(1) }
                repeat(repeats) { yield(0) }
                repeat(repeats) { yield(-1) }
            }
        }.drop(1)

        val fftSequence = generateSequence(initialSignal) { signal ->
            signal.indices.map { index ->
                fftPattern(index)
                    .zip(signal.asSequence(), Int::times)
                    .sum()
                    .absoluteValue % 10
            }
        }

        return fftSequence
            .drop(phases)
            .first()
            .take(8)
            .joinToString(separator = "")
    }
}