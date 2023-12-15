package org.chalup.advent2023

import org.chalup.advent2023.Day15.Instruction.AdjustLens
import org.chalup.advent2023.Day15.Instruction.RemoveLens

object Day15 {
    fun task1(input: List<String>): Int = input
        .single()
        .splitToSequence(',')
        .sumOf { calculateHash(it) }

    fun task2(input: List<String>): Int = input
        .single()
        .splitToSequence(',')
        .map { parseInstruction(it) }
        .let { instructions ->
            List(256) { Box() }
                .also { boxes ->
                for (instruction in instructions) {
                    when (instruction) {
                        is RemoveLens -> boxes[calculateHash(instruction.label)]
                            .lenses
                            .removeAll { it.label == instruction.label }

                        is AdjustLens -> boxes[calculateHash(instruction.lens.label)]
                            .lenses
                            .apply {
                                val lensIndex = indexOfFirst { it.label == instruction.lens.label }

                                if (lensIndex != -1) {
                                    set(lensIndex, instruction.lens)
                                } else {
                                    add(instruction.lens)
                                }
                             }
                    }
                }
            }
        }
        .flatMapIndexed { boxIndex: Int, box: Box ->
            box.lenses.mapIndexed { lensIndex, lens ->
                (boxIndex + 1) * (lensIndex + 1) * lens.focalLength
            }
        }
        .sum()

    private fun calculateHash(input: String) = input.fold(0) { acc, char ->
        acc
            .plus(char.code.also { check(it in 0..255) })
            .times(17)
            .mod(256)
    }

    private data class Lens(
        val label: String,
        val focalLength: Int,
    )

    private data class Box(val lenses: MutableList<Lens> = mutableListOf())

    private fun parseInstruction(input: String) =
        if (input.endsWith('-')) {
            RemoveLens(input.dropLast(1))
        } else {
            AdjustLens(
                Lens(
                    label = input.substringBefore('='),
                    focalLength = input.substringAfter('=').toInt()
                )
            )
        }

    private sealed interface Instruction {
        data class RemoveLens(val label: String) : Instruction
        data class AdjustLens(val lens: Lens) : Instruction
    }
}