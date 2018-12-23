package org.chalup.advent2018

import org.chalup.advent2018.Cpu.Opcode
import org.chalup.utils.parseNumbers

object Day16 {
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