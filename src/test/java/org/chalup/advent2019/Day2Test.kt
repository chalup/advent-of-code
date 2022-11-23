package org.chalup.advent2019

import com.google.common.truth.Truth
import io.kotest.core.spec.style.FreeSpec
import org.chalup.advent2019.IntcodeInterpreter.Companion.parseProgram
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.Finished

internal class Day2Test : FreeSpec({
    val programs = listOf(
        "1,0,0,0,99" to "2,0,0,0,99",
        "2,3,0,3,99" to "2,3,0,6,99",
        "2,4,4,5,99,0" to "2,4,4,5,99,9801",
        "1,1,1,4,99,5,6,0,99" to "30,1,1,4,2,5,6,0,99"
    )

    for ((input: String, expectedOutput: String) in programs) {
        "should execute program $input" {
            Truth
                .assertThat(parseProgram(input).let { IntcodeInterpreter(it).run() as Finished }.finalState)
                .isEqualTo(IntcodeInterpreter.Memory(parseProgram(expectedOutput)))
        }
    }
})