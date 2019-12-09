package org.chalup.advent2019

import com.google.common.truth.Truth
import org.chalup.advent2019.IntcodeInterpreter.Companion.parseProgram
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.Finished
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class Day9Test {
    @ParameterizedTest
    @MethodSource("programExecutionData")
    fun `should execute full set of intcode computer instructions`(input: String, expectedOutputs: List<Int>) {
        Truth
            .assertThat(IntcodeInterpreter.runProgram(input))
            .containsExactlyElementsIn(expectedOutputs)
            .inOrder()
    }

    companion object {
        @JvmStatic
        fun programExecutionData() = listOf<Arguments>(
            Arguments.arguments("109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99", listOf<Long>(109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99)),
            Arguments.arguments("1102,34915192,34915192,7,4,7,99,0", listOf(1219070632396864)),
            Arguments.arguments("104,1125899906842624,99", listOf(1125899906842624))
        )
    }
}