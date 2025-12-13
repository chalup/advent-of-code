package org.chalup.advent2025

import java.util.PriorityQueue

object Day10 {
    fun task1(input: List<String>): Int {
        val specs = input.map(::parseSpecs)

        return specs
            .sumOf { it.shortestInitializationSequence() }
    }

    fun MachineSpec.shortestInitializationSequence(): Int {
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

    fun parseSpecs(text: String): MachineSpec {
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

        val joltages = text
            .substringAfter("{")
            .substringBefore("}")
            .split(",")
            .map(String::toInt)

        return MachineSpec(targetState, buttonsMasks, joltages)
    }

    data class MachineSpec(
        val targetState: UInt,
        val buttonsMasks: List<UInt>,
        val joltages: List<Int>,
    ) {
        override fun toString(): String {
            return "MachineSpec(targetState=${targetState.toString(radix = 2)}, buttonsMasks=${buttonsMasks.map { it.toString(radix = 2) }}, joltages=$joltages)"
        }
    }
}