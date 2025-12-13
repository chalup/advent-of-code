package org.chalup.advent2025

import java.util.PriorityQueue

object Day10 {
    fun task1(input: List<String>): Int {
        data class InitializationSpec(
            val targetState: UInt,
            val buttonsMasks: List<UInt>,
        )

        val specs = input.map { text ->
            val targetState = text
                .substringAfter('[')
                .substringBefore(']')
                .withIndex()
                .sumOf { (i, c) ->
                    if (c == '#') 1U shl i else 0U
                }

            val buttonsMasks = text
                .substringAfter("] ")
                .substringBefore(" {")
                .split(" ")
                .map { buttonSpec ->
                    buttonSpec
                        .substringAfter('(')
                        .substringBefore(')')
                        .split(",")
                        .map(String::toInt)
                        .sumOf { 1U shl it }
                }

            InitializationSpec(targetState, buttonsMasks)
        }

        fun InitializationSpec.shortestInitializationSequence(): Int {
            val queue = PriorityQueue<Pair<UInt, Int>>(compareBy { (_, clicks) -> clicks })
                .apply { add(0U to 0) }

            while (queue.isNotEmpty()) {
                val (state, clicks) = queue.poll()

                if (state == targetState) return clicks

                buttonsMasks
                    .mapTo(queue) { buttonMask ->
                        (state xor buttonMask) to (clicks + 1)
                    }
            }

            throw IllegalStateException("Could not find initialization sequence!")
        }

        return specs
            .sumOf { it.shortestInitializationSequence() }
    }

    fun task2(input: List<String>): Int {
        data class JoltageSpec(
            val buttons: List<Set<Int>>,
            val joltages: List<Int>,
        )

        val specs = input.map { text ->
            val buttons = text
                .substringAfter("] ")
                .substringBefore(" {")
                .split(" ")
                .map { buttonSpec ->
                    buttonSpec
                        .substringAfter('(')
                        .substringBefore(')')
                        .split(",")
                        .mapTo(mutableSetOf(), String::toInt)
                }

            val joltages = text
                .substringAfter("{")
                .substringBefore("}")
                .split(",")
                .map(String::toInt)

            JoltageSpec(buttons, joltages)
        }

        return TODO()
    }
}