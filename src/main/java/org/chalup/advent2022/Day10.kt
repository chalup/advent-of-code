package org.chalup.advent2022

import org.chalup.utils.match

object Day10 {
    fun task1(input: List<String>) = registerValuesSequence(input)
        .let {
            val keyCycles = setOf(20, 60, 100, 140, 180, 220)

            it.mapIndexedNotNull { index, i ->
                val cycleIndex = index + 1

                (cycleIndex * i).takeIf { cycleIndex in keyCycles }
            }
        }
        .sum()

    fun task2(input: List<String>) = registerValuesSequence(input)
        .mapIndexed { index, registerX ->
            (index % 40) in (registerX - 1)..(registerX + 1)
        }
        .map { if (it) '#' else '.' }
        .chunked(40)
        .joinToString(separator = "\n") { it.joinToString(separator = "") }
        .let {
            buildString {
                appendLine()
                append(it)
                appendLine()
            }
        }
}

private fun registerValuesSequence(input: List<String>) = input
    .map {
        match(it) {
            literal("noop", CpuInstruction.Noop)
            pattern("""addx (\-?\d+)""") { (number) -> CpuInstruction.AddX(number.toInt()) }
        }
    }
    .let { instructions ->
        sequence {
            var registerX = 1

            for (i in instructions) {
                when (i) {
                    CpuInstruction.Noop -> yield(registerX)
                    is CpuInstruction.AddX -> {
                        yield(registerX)
                        yield(registerX)
                        registerX += i.value
                    }
                }
            }
        }
    }

private sealed interface CpuInstruction {
    object Noop : CpuInstruction
    data class AddX(val value: Int) : CpuInstruction
}