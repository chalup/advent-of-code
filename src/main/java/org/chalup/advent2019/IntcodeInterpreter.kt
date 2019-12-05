package org.chalup.advent2019

import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.ExecutionError.UnknownOpcode
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.Finished

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