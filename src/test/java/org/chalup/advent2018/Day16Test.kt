package org.chalup.advent2018

import com.google.common.truth.Truth.assertThat
import org.chalup.advent2018.Cpu.Opcode.ADDI
import org.chalup.advent2018.Cpu.Opcode.MULR
import org.chalup.advent2018.Cpu.Opcode.SETI
import org.chalup.advent2018.Day16.TestResult
import org.junit.jupiter.api.Test

class Day16Test {
    @Test
    fun `should perform the opcode test`() {
        val input = """
                    Before: [3, 2, 1, 1]
                    9 2 1 2
                    After:  [3, 2, 2, 1]
                    """.trimIndent().lines()

        val testResult = Day16
            .parseTestInput(input)
            .first()
            .let { Day16.testInstruction(it) }


        assertThat(testResult).isEqualTo(TestResult(9, setOf(MULR, ADDI, SETI)))
    }
}