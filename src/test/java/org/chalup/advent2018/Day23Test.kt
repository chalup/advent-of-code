package org.chalup.advent2018

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class Day23Test {
    @Test
    fun `should calculate total risk`() {
        val testInput = """
            pos=<0,0,0>, r=4
            pos=<1,0,0>, r=1
            pos=<4,0,0>, r=3
            pos=<0,2,0>, r=1
            pos=<0,5,0>, r=3
            pos=<0,0,3>, r=1
            pos=<1,1,1>, r=1
            pos=<1,1,2>, r=1
            pos=<1,3,1>, r=1
        """.trimIndent().lines()
        assertThat(Day23.part1(testInput)).isEqualTo(7)
    }
}