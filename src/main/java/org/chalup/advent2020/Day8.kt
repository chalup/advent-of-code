package org.chalup.advent2020

import org.chalup.utils.match

object Day8 {
    fun task1(input: List<String>): Int = input
        .map(::parseInstruction)
        .let { program ->
            Handheld.loadProgram(program)

            val executedInstructions = mutableSetOf<Int>()

            do {
                executedInstructions += Handheld.instructionIndex
                Handheld.step()
            } while (Handheld.instructionIndex !in executedInstructions)

            Handheld.accumulator
        }

    fun task2(input: List<String>): Int = input
        .map(::parseInstruction)
        .let { originalProgram ->
            fun executeProgram(program: List<Instruction>): Int? {
                Handheld.loadProgram(program)

                val executedInstructions = mutableSetOf<Int>()

                do {
                    executedInstructions += Handheld.instructionIndex
                    Handheld.step()
                } while (Handheld.instructionIndex !in executedInstructions && !Handheld.terminated)


                return Handheld.accumulator.takeIf { Handheld.terminated }
            }

            originalProgram
                .mapIndexedNotNull { index, instruction ->
                    fun swapInstruction(replacement: Instruction) =
                        originalProgram.toMutableList().apply { set(index, replacement) }

                    when(instruction) {
                        is Instruction.NoOp -> swapInstruction(Instruction.Jump(instruction.argument))
                        is Instruction.Acc -> null
                        is Instruction.Jump -> swapInstruction(Instruction.NoOp(instruction.offset))
                    }
                }
                .mapNotNull { program -> executeProgram(program) }
                .first()
        }
}

object Handheld {
    var accumulator = 0
    var instructionIndex = 0
    var instructions = emptyList<Instruction>()

    val terminated: Boolean
        get() = instructionIndex >= instructions.size

    fun loadProgram(program: List<Instruction>) {
        accumulator = 0
        instructionIndex = 0
        instructions = program
    }

    fun step() {
        when (val instruction = instructions[instructionIndex]) {
            is Instruction.NoOp -> instructionIndex += 1
            is Instruction.Acc -> {
                instructionIndex += 1
                accumulator += instruction.change
            }
            is Instruction.Jump -> instructionIndex += instruction.offset
        }
    }
}

private fun parseInstruction(input: String) = match<Instruction>(input) {
    pattern("nop ([+-]\\d+)") { (argument) -> Instruction.NoOp(argument.toInt()) }
    pattern("""acc ([+-]\d+)""") { (change) -> Instruction.Acc(change.toInt()) }
    pattern("""jmp ([+-]\d+)""") { (offset) -> Instruction.Jump(offset.toInt()) }
}

sealed class Instruction {
    data class NoOp(val argument: Int) : Instruction()
    data class Acc(val change: Int) : Instruction()
    data class Jump(val offset: Int) : Instruction()
}
