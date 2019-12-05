package org.chalup.advent2019

import org.chalup.advent2019.Day2.IntcodeInterpreter.ProgramResult.ExecutionError.UnknownOpcode
import org.chalup.advent2019.Day2.IntcodeInterpreter.ProgramResult.Finished
import org.chalup.utils.times

object Day2 {
    object IntcodeInterpreter {
        fun execute(program: List<Int>): ProgramResult {
            val memory = program.toMutableList()
            var ip = 0

            while (true) {
                when (memory[ip]) {
                    1 -> {
                        memory[memory[ip + 3]] = memory[memory[ip + 1]] + memory[memory[ip + 2]]
                        ip += 4
                    }
                    2 -> {
                        memory[memory[ip + 3]] = memory[memory[ip + 1]] * memory[memory[ip + 2]]
                        ip += 4
                    }
                    99 -> return Finished(finalState = memory)
                    else -> return UnknownOpcode(ip, dump = memory)
                }
            }
        }

        sealed class ProgramResult {
            sealed class ExecutionError : ProgramResult() {
                data class UnknownOpcode(val ip: Int, val dump: List<Int>) : ExecutionError()
            }

            data class Finished(val finalState: List<Int>) : ProgramResult()
        }
    }

    fun parseProgram(input: String) = input.split(",").map { it.toInt() }

    private fun List<Int>.tweak(noun: Int, verb: Int): List<Int> = toMutableList().apply {
        set(1, noun)
        set(2, verb)
    }

    fun task1(input: String): Int = parseProgram(input)
        .tweak(noun = 12, verb = 2)
        .let {
            when (val result = IntcodeInterpreter.execute(it)) {
                is UnknownOpcode -> throw IllegalStateException("Unknown opcode ${result.dump[result.ip]} at ${result.ip}")
                is Finished -> result.finalState[0]
            }
        }

    fun task2(input: String, expectedOutput: Int): Int {
        val program = parseProgram(input)

        return ((0..99) * (0..99))
            .firstOrNull { (noun, verb) ->
                when (val result = IntcodeInterpreter.execute(program.tweak(noun, verb))) {
                    is UnknownOpcode -> false
                    is Finished -> result.finalState[0] == expectedOutput
                }
            }
            ?.let { (noun, verb) -> noun * 100 + verb }
            ?: throw IllegalArgumentException("Cannot find the noun & verb for the supplied program and desired output")
    }
}