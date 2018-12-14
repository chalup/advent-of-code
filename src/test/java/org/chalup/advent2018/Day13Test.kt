package org.chalup.advent2018

import com.google.common.truth.Truth.assertThat
import org.chalup.advent2018.Day13.State
import org.chalup.utils.Point
import org.junit.jupiter.api.Test

class Day13Test {
    @Test
    fun `should find the location of the first crash`() {
        assertThat(
            Day13
                .simulate(State(testInput))
                .dropWhile { it.crashes.isEmpty() }
                .first()
                .crashes.single()
        ).isEqualTo(Point(7, 3))
    }

    companion object {
        val testInput = """
                        /->-\
                        |   |  /----\
                        | /-+--+-\  |
                        | | |  | v  |
                        \-+-/  \-+--/
                          \------/
                        """.trimIndent().lines()
    }
}