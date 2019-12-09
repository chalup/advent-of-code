package org.chalup.advent2019

import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.ExecutionError
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.Finished
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.GeneratedOutput

object Day7 {
    fun calculateMaxThrusterInput(programInput: String): Long {
        val program = IntcodeInterpreter.parseProgram(programInput)
        val phaseConfigs = permutations((0 until 5).toSet())

        return phaseConfigs
            .map { config -> calculateThrust(program, config) }
            .max()!!
    }

    private fun calculateThrust(program: List<Long>, phaseConfig: List<Int>): Long {
        return phaseConfig.fold(0L) { signal, phase ->
            val interpreter = IntcodeInterpreter(program).apply {
                sendInput(phase.toLong())
                sendInput(signal)
            }

            when (val result = interpreter.run()) {
                is Finished -> throw IllegalStateException("Intcode program halted prematurely")
                is ExecutionError -> throw IllegalStateException(result.getErrorMessage())
                is GeneratedOutput -> result.output
            }
        }
    }

    fun calculateMaxThrusterInputWithFeedbackLoop(programInput: String): Long {
        val program = IntcodeInterpreter.parseProgram(programInput)

        val phaseConfigs = permutations((5..9).toSet())

        return phaseConfigs
            .map { phaseConfig ->
                val amps = phaseConfig.map { phase -> IntcodeInterpreter(program).apply { sendInput(phase.toLong()) } }

                var signal: Long? = 0
                var output = 0L

                while(signal != null) {
                    output = signal

                    for (amp in amps) {
                        if (signal != null) amp.sendInput(signal)

                        signal = when(val result = amp.run()) {
                            is ExecutionError -> throw IllegalStateException(result.getErrorMessage())
                            is GeneratedOutput -> result.output
                            is Finished -> null
                        }
                    }
                }

                output
            }
            .max()!!
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