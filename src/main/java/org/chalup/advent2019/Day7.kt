package org.chalup.advent2019

import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.ExecutionError
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.Finished

object Day7 {
    fun calculateMaxThrusterInput(programInput: String): Int {
        val program = programInput.split(",").map(String::toInt)

        val phaseConfigs = permutations((0 until 5).toSet())

        return phaseConfigs
            .map { config -> calculateThrust(program, config) }
            .max()!!
    }

    private fun calculateThrust(program: List<Int>, phaseConfig: List<Int>): Int {
        return phaseConfig.fold(0) { signal, phase ->
            when (val result = IntcodeInterpreter.execute(program, phase, signal)) {
                is Finished -> result.outputs.first()
                is ExecutionError -> throw IllegalStateException(result.getErrorMessage())
            }
        }
    }

    private fun <T : Any> permutations(set: Set<T>) = sequence {
        fun helper(head: List<T>, elements: Set<T>): Sequence<List<T>> = sequence {
            if (elements.isEmpty()) yield(head)
            else {
                for (e in elements) {
                    yieldAll(helper(head + e, elements - e))
                }
            }
        }

        yieldAll(helper(emptyList(), set))
    }
}