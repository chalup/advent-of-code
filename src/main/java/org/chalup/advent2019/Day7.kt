package org.chalup.advent2019

import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.ExecutionError
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.Finished
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.concurrent.thread

object Day7 {
    fun calculateMaxThrusterInput(programInput: String): Int {
        val program = programInput.split(",").map(String::toInt)

        val phaseConfigs = permutations((0 until 5).toSet())

        return phaseConfigs
            .map { config -> calculateThrust(program, config) }
            .max()!!
    }

    fun calculateMaxThrusterInputWithFeedbackLoop(programInput: String): Int {
        val program = programInput.split(",").map(String::toInt)

        val phaseConfigs = permutations((5..9).toSet())

        return phaseConfigs
            .map { phaseConfig ->
                val programs = pipeline(program, phaseConfig)
                val input = programs.first().input
                val output = programs.last().output
                var result = 0

                input.put(0)
                while (programs.any { it.isRunning() }) {
                    val pipelineOutput = output.poll(1, MILLISECONDS)
                    if (pipelineOutput != null) {
                        input.put(pipelineOutput)
                        result = pipelineOutput
                    }
                }

                result
            }
            .max()!!
    }

    private fun pipeline(program: List<Int>, phases: List<Int>): List<IntcodeProgram> {
        val inputs = phases.map { phase -> LinkedBlockingQueue<Int>(listOf(phase)) }
        val outputs = inputs.drop(1).toMutableList().apply { add(LinkedBlockingQueue()) }

        return inputs.zip(outputs) { input, output -> IntcodeProgram(program, input, output) }
    }

    private class IntcodeProgram(val program: List<Int>,
                                 val input: BlockingQueue<Int>,
                                 val output: BlockingQueue<Int>) {
        private val thread = thread {
            when (val result = IntcodeInterpreter.execute(program, input, output)) {
                is ExecutionError -> throw RuntimeException(result.getErrorMessage())
                is Finished -> Unit
            }
        }

        fun isRunning() = thread.isAlive
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