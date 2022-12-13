package org.chalup.advent2022

import org.chalup.utils.textBlocks

object Day5 {
    fun task1(input: List<String>) = parse(input)
        .let { (stacks, instructions) ->
            for (instruction in instructions) {
                repeat(instruction.count) {
                    stacks[instruction.to - 1].add(stacks[instruction.from - 1].removeLast())
                }
            }

            stacks.joinToString(separator = "") { it.last().toString() }
        }

    fun task2(input: List<String>) = parse(input)
        .let { (stacks, instructions) ->
            for (instruction in instructions) {
                val crates = stacks[instruction.from - 1].takeLast(instruction.count)
                repeat(instruction.count) { stacks[instruction.from - 1].removeLast() }

                stacks[instruction.to - 1].addAll(crates)
            }

            stacks.joinToString(separator = "") { it.last().toString() }
        }

    private fun parse(input: List<String>): InputData {
        val (stacksBlock, instructionsBlock) = textBlocks(input)

        val numberOfStacks = stacksBlock.last()
            .trim()
            .split("""\s+""".toRegex())
            .last().toInt()

        val stacks = List(numberOfStacks) { mutableListOf<Char>() }
        stacksBlock
            .asReversed()
            .drop(1)
            .forEach { line ->
                repeat(numberOfStacks) { i ->
                    line.getOrNull(i * 4 + 1)
                        ?.takeUnless { it == ' ' }
                        ?.run { stacks[i].add(this) }
                }
            }

        val instructions = instructionsBlock
            .map { line ->
                val (count, from, to) = line.split(' ').mapNotNull { it.toIntOrNull() }
                CraneInstructions(count, from, to)
            }

        return InputData(stacks, instructions)
    }

    private data class InputData(
        val stacks: List<MutableList<Char>>,
        val instructions: List<CraneInstructions>
    )

    private data class CraneInstructions(
        val count: Int,
        val from: Int,
        val to: Int
    )
}