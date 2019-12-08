package org.chalup.advent2019

import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.ExecutionError
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.Finished
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.GeneratedOutput

object Day5 {
    fun task1(input: String) = runDiagnostics(programInput = input, systemId = 1)
    fun task2(input: String) = runDiagnostics(programInput = input, systemId = 5)

    private fun parseProgram(input: String) = input.split(",").map { it.toInt() }

    private fun runDiagnostics(systemId: Int, programInput: String): Int {
        val interpreter = IntcodeInterpreter(parseProgram(programInput)).apply { sendInput(systemId) }

        val outputs = mutableListOf<Int>()

        do {
            val result = interpreter.run()

            when (result) {
                is ExecutionError -> throw IllegalStateException(result.getErrorMessage())
                is GeneratedOutput -> outputs += result.output
                is Finished -> Unit
            }
        } while (result !is Finished)

        check(outputs.dropLast(1).all { it == 0 }) { "Expected all but last output to be 0" }

        return outputs.last()
    }
}