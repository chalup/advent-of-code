package org.chalup.advent2019

import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.ExecutionError
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.Finished

object Day5 {
    fun task1(input: String) = runDiagnostics(programInput = input, systemId = 1)
    fun task2(input: String) = runDiagnostics(programInput = input, systemId = 5)

    private fun parseProgram(input: String) = input.split(",").map { it.toInt() }

    private fun runDiagnostics(systemId: Int, programInput: String) =
        parseProgram(programInput).let { program ->
            when (val result = IntcodeInterpreter.execute(program, input = systemId)) {
                is ExecutionError -> throw IllegalStateException(result.getErrorMessage())
                is Finished -> result.outputs
            }
        }
}