package org.chalup.advent2018

import org.chalup.advent2018.Cpu.Instruction
import org.chalup.advent2018.Cpu.Opcode
import org.chalup.advent2018.Cpu.Program
import kotlin.math.roundToInt
import kotlin.math.sqrt

object Day19 {
    fun parseProgram(input: List<String>) =
        Program(instructions = input.drop(1).map { parseInstruction(it) },
                instructionPointerBinding = parseBinding(input.first()))

    fun part1(input: List<String>) =
        Cpu(numberOfRegisters = 6)
            .apply {
                executeProgram(
                    parseProgram(input).copy(
                        specialHandlers = mapOf(1 to Cpu::calculateSumOfFactors)
                    )
                )
            }
            .registers[0]

    fun part2(input: List<String>) =
        Cpu(numberOfRegisters = 6)
            .apply { registers[0] = 1 }
            .apply {
                executeProgram(
                    parseProgram(input).copy(
                        specialHandlers = mapOf(1 to Cpu::calculateSumOfFactors)
                    )
                )
            }
            .registers[0]

    private fun parseBinding(bindingInput: String) = bindingInput.split(' ').last().toInt()

    private fun parseInstruction(instructionInput: String) = instructionInput.split(' ').let { elements ->
        val opcode = Opcode.valueOf(elements.first().toUpperCase())
        val params = elements.drop(1).map { it.toInt() }

        Instruction(opcode, params)
    }
}

private fun Cpu.calculateSumOfFactors() {
    val x = registers[1]

    registers[0] = (1..x.sqrt()).sumBy { if (x % it == 0) it + x / it else 0 }
    registers[2] = x + 1
    registers[3] = 1
    registers[4] = x + 1
    registers[5] = 256
}

fun Int.sqrt() = sqrt(toDouble()).roundToInt()