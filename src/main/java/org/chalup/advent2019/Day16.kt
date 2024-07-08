package org.chalup.advent2019

import kotlin.math.absoluteValue

object Day16 {
    fun task1(input: List<String>): String = fft(
        initialSignal = input.single().map { it.digitToInt() },
        phases = 100
    )

    fun task2(input: List<String>): String = fft(
        initialSignal = input
            .single()
            .map { it.digitToInt() }
            .let { signal ->
                buildList {
                    repeat(10000) {
                        addAll(signal)
                    }
                }
            },
        phases = 100
    )

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
            .onEachIndexed { index, ints -> println("Phase $index, signalSize = ${ints.size}") }
            .drop(phases)
            .first()
            .take(8)
            .joinToString(separator = "")
    }
}