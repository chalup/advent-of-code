package org.chalup.advent2019

import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.ExecutionError
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.Finished
import java.util.concurrent.LinkedBlockingQueue

object Day5 {
    fun task1(input: String) = runDiagnostics(programInput = input, systemId = 1)
    fun task2(input: String) = runDiagnostics(programInput = input, systemId = 5)

    private fun parseProgram(input: String) = input.split(",").map { it.toInt() }

    private fun runDiagnostics(systemId: Int, programInput: String): Int =
        parseProgram(programInput).let { program ->
            val input = LinkedBlockingQueue(listOf(systemId))
            val output = LinkedBlockingQueue<Int>()

            when (val result = IntcodeInterpreter.execute(program, input, output)) {
                is ExecutionError -> throw IllegalStateException(result.getErrorMessage())
                is Finished -> output.last()
            }
        }
}