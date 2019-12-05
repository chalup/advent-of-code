package org.chalup.advent2019

import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.ExecutionError
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.Finished
import org.chalup.utils.times

object Day2 {
    fun parseProgram(input: String) = input.split(",").map { it.toInt() }

    private fun List<Int>.tweak(noun: Int, verb: Int): List<Int> = toMutableList().apply {
        set(1, noun)
        set(2, verb)
    }

    fun task1(input: String): Int = parseProgram(input)
        .tweak(noun = 12, verb = 2)
        .let {
            when (val result = IntcodeInterpreter.execute(it)) {
                is ExecutionError -> throw IllegalStateException(result.getErrorMessage())
                is Finished -> result.finalState[0]
            }
        }

    fun task2(input: String, expectedOutput: Int): Int {
        val program = parseProgram(input)

        return ((0..99) * (0..99))
            .firstOrNull { (noun, verb) ->
                when (val result = IntcodeInterpreter.execute(program.tweak(noun, verb))) {
                    is ExecutionError -> false
                    is Finished -> result.finalState[0] == expectedOutput
                }
            }
            ?.let { (noun, verb) -> noun * 100 + verb }
            ?: throw IllegalArgumentException("Cannot find the noun & verb for the supplied program and desired output")
    }
}