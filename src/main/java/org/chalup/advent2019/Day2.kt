package org.chalup.advent2019

import org.chalup.advent2019.IntcodeInterpreter.Companion.parseProgram
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.ExecutionError
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.Finished
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.GeneratedOutput
import org.chalup.utils.times

object Day2 {
    private fun List<Long>.tweak(noun: Long, verb: Long): List<Long> = toMutableList().apply {
        set(1, noun)
        set(2, verb)
    }

    fun task1(input: String): Long = parseProgram(input)
        .tweak(noun = 12, verb = 2)
        .let {
            when (val result = IntcodeInterpreter(it).run()) {
                is ExecutionError -> throw IllegalStateException(result.getErrorMessage())
                is Finished -> result.finalState[0]
                is GeneratedOutput -> throw IllegalStateException("Didn't expect any output from this program")
            }
        }

    fun task2(input: String, expectedOutput: Long): Int {
        val program = parseProgram(input)

        return ((0..99) * (0..99))
            .firstOrNull { (noun, verb) ->
                when (val result = IntcodeInterpreter(program.tweak(noun.toLong(), verb.toLong())).run()) {
                    is ExecutionError -> false
                    is Finished -> result.finalState[0] == expectedOutput
                    is GeneratedOutput -> false
                }
            }
            ?.let { (noun, verb) -> noun * 100 + verb }
            ?: throw IllegalArgumentException("Cannot find the noun & verb for the supplied program and desired output")
    }
}