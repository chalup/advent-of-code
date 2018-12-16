package org.chalup.advent2018

import org.chalup.advent2018.Day16.Opcode.ADDI
import org.chalup.advent2018.Day16.Opcode.ADDR
import org.chalup.advent2018.Day16.Opcode.BANI
import org.chalup.advent2018.Day16.Opcode.BANR
import org.chalup.advent2018.Day16.Opcode.BORI
import org.chalup.advent2018.Day16.Opcode.BORR
import org.chalup.advent2018.Day16.Opcode.EQIR
import org.chalup.advent2018.Day16.Opcode.EQRI
import org.chalup.advent2018.Day16.Opcode.EQRR
import org.chalup.advent2018.Day16.Opcode.GTIR
import org.chalup.advent2018.Day16.Opcode.GTRI
import org.chalup.advent2018.Day16.Opcode.GTRR
import org.chalup.advent2018.Day16.Opcode.MULI
import org.chalup.advent2018.Day16.Opcode.MULR
import org.chalup.advent2018.Day16.Opcode.SETI
import org.chalup.advent2018.Day16.Opcode.SETR

object Day16 {
    enum class Opcode {
        ADDR, ADDI,
        MULR, MULI,
        BANR, BANI,
        BORR, BORI,
        SETR, SETI,
        GTIR, GTRI, GTRR,
        EQIR, EQRI, EQRR
    }

    data class Cpu(val registers: MutableList<Int> = MutableList(4) { 0 }) {
        fun execute(opcode: Opcode, params: List<Int>) {
            val (inA, inB, out) = params

            registers[out] = when (opcode) {
                ADDR -> registers[inA] + registers[inB]
                ADDI -> registers[inA] + inB
                MULR -> registers[inA] * registers[inB]
                MULI -> registers[inA] * inB
                BANR -> registers[inA] and registers[inB]
                BANI -> registers[inA] and inB
                BORR -> registers[inA] or registers[inB]
                BORI -> registers[inA] or inB
                SETR -> registers[inA]
                SETI -> inA
                GTIR -> if (inA > registers[inB]) 1 else 0
                GTRI -> if (registers[inA] > inB) 1 else 0
                GTRR -> if (registers[inA] > registers[inB]) 1 else 0
                EQIR -> if (inA == registers[inB]) 1 else 0
                EQRI -> if (registers[inA] == inB) 1 else 0
                EQRR -> if (registers[inA] == registers[inB]) 1 else 0
            }
        }
    }

    data class TestResult(val opcodeNumber: Int,
                          val possibleOpcodes: Set<Opcode>)

    fun testInstruction(testInput: TestInput): TestResult = with(testInput) {
        Opcode
            .values()
            .filter { Cpu(before.toMutableList()).apply { execute(it, data.drop(1)) } == Cpu(after.toMutableList()) }
            .toSet()
            .let { TestResult(data.first(), it) }
    }

    data class TestInput(val before: List<Int>,
                         val data: List<Int>,
                         val after: List<Int>)

    fun parseNumbers(input: String) =
        "\\d+".toRegex()
            .findAll(input)
            .map { it.value.toInt() }
            .toList()

    fun parseTestInput(lines: List<String>): List<TestInput> =
        lines
            .chunked(4)
            .takeWhile { (firstLine) -> firstLine.isNotBlank() }
            .map { chunk ->
                chunk
                    .take(3)
                    .map { parseNumbers(it) }
                    .let { (before, data, after) ->
                        TestInput(before = before,
                                  data = data,
                                  after = after)
                    }
            }

    fun deriveOpcodesMapping(testResults: List<TestResult>): Map<Int, Opcode> =
        testResults
            .groupBy { it.opcodeNumber }
            .mapValues { (_, testResults) ->
                testResults
                    .map { it.possibleOpcodes }
                    .reduce { acc, set -> acc intersect set }
            }
            .let { reducedTestResults ->
                fun Map.Entry<*, Set<Opcode>>.isResolved() = value.size == 1

                generateSequence(reducedTestResults) { data ->
                    if (data.all { it.isResolved() }) return@generateSequence null

                    val resolvedOpcodes = data
                        .filter { it.isResolved() }
                        .values
                        .flatten()

                    data.mapValues {
                        if (it.isResolved()) {
                            it.value
                        } else {
                            (it.value - resolvedOpcodes)
                        }
                    }
                }
            }
            .last()
            .mapValues { (_, opcodes) -> opcodes.single() }

    fun Cpu.executeProgram(instructions: List<List<Int>>, opcodesMapping: Map<Int, Opcode>) =
        instructions.forEach { instruction ->
            execute(opcodesMapping.getValue(instruction.first()), instruction.takeLast(3))
        }

    fun parseTestProgram(input: List<String>): List<List<Int>> =
        input
            .chunked(2)
            .dropWhile { lines -> lines.any { it.isNotBlank() } }
            .flatten()
            .drop(2)
            .map { parseNumbers(it) }
}