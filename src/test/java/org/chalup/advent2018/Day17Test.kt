package org.chalup.advent2018

import com.google.common.truth.Truth.assertThat
import org.chalup.advent2018.Day16.Opcode.ADDI
import org.chalup.advent2018.Day16.Opcode.MULR
import org.chalup.advent2018.Day16.Opcode.SETI
import org.chalup.advent2018.Day16.TestResult
import org.junit.jupiter.api.Test

class Day17Test {
    @Test
    fun `should calculate the amount of running water`() {
        assertThat(Day17.part1(testInput)).isEqualTo(57)
    }

    @Test
    fun `should calculate the amount of retained water`() {
        assertThat(Day17.part2(testInput)).isEqualTo(29)
    }

    companion object {
        val testInput = """
                            x=495, y=2..7
                            y=7, x=495..501
                            x=501, y=3..7
                            x=498, y=2..4
                            x=506, y=1..2
                            x=498, y=10..13
                            x=504, y=10..13
                            y=13, x=498..504
                        """.trimIndent().lines()
    }
}