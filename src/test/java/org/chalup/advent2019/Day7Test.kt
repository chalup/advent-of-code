package org.chalup.advent2019

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class Day7Test {
    @ParameterizedTest
    @MethodSource("programExecutionData")
    fun `should calculate correct max thrust`(program: String, expectedMaxTrust: Int) {
        assertThat(Day7.calculateMaxThrusterInput(program)).isEqualTo(expectedMaxTrust)
    }

    companion object {
        @JvmStatic
        fun programExecutionData() = listOf<Arguments>(
            Arguments.arguments("3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0", 43210),
            Arguments.arguments("3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0", 54321),
            Arguments.arguments("3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0", 65210)
        )
    }
}