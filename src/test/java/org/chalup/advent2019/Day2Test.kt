package org.chalup.advent2019

import com.google.common.truth.Truth
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.Finished
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class Day2Test {
    @ParameterizedTest
    @MethodSource("programExecutionData")
    fun `should execute program`(input: String, expectedOutput: String) {
        Truth
            .assertThat(Day2.parseProgram(input).let { IntcodeInterpreter(it).run() as Finished }.finalState)
            .isEqualTo(Day2.parseProgram(expectedOutput))
    }

    companion object {
        @JvmStatic
        fun programExecutionData() = listOf<Arguments>(
            Arguments.arguments("1,0,0,0,99", "2,0,0,0,99"),
            Arguments.arguments("2,3,0,3,99", "2,3,0,6,99"),
            Arguments.arguments("2,4,4,5,99,0", "2,4,4,5,99,9801"),
            Arguments.arguments("1,1,1,4,99,5,6,0,99", "30,1,1,4,2,5,6,0,99")
        )
    }
}