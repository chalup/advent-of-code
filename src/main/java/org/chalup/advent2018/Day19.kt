package org.chalup.advent2018

import org.chalup.advent2018.Cpu.Companion.parseProgram
import kotlin.math.roundToInt
import kotlin.math.sqrt

object Day19 {
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
}

private fun Cpu.calculateSumOfFactors() {
    val x = registers[1]

    registers[0] = (1..x.sqrt()).sumOf { if (x % it == 0) it + x / it else 0 }
    registers[2] = x + 1
    registers[3] = 1
    registers[4] = x + 1
    registers[5] = 256
}

fun Int.sqrt() = sqrt(toDouble()).roundToInt()