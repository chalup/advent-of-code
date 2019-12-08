package org.chalup.advent2019

import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.ExecutionError
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.Finished
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

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
            val input = LinkedBlockingQueue<Int>(listOf(phase, signal))
            val output = LinkedBlockingQueue<Int>()

            when (val result = IntcodeInterpreter.execute(program, input, output)) {
                is Finished -> output.take()
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