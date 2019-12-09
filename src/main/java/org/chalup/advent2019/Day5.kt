package org.chalup.advent2019

object Day5 {
    fun task1(input: String) = runDiagnostics(programInput = input, systemId = 1)
    fun task2(input: String) = runDiagnostics(programInput = input, systemId = 5)

    private fun runDiagnostics(systemId: Int, programInput: String): Int = IntcodeInterpreter
        .runProgram(programInput, systemId)
        .also { outputs -> check(outputs.dropLast(1).all { it == 0 }) { "Expected all but last output to be 0" } }
        .last()
}