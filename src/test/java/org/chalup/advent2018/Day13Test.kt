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

    @Test
    fun `should find the location of magically cleaned crashes`() {
        assertThat(
            Day13
                .simulate(State(magicCleanInput), cleanupCrashes = true)
                .dropWhile { it.carts.size > 1 }
                .first()
                .carts.single().position
        ).isEqualTo(Point(6, 4))
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

        val magicCleanInput = """
                              />-<\
                              |   |
                              | /<+-\
                              | | | v
                              \>+</ |
                                |   ^
                                \<->/
                              """.trimIndent().lines()
    }
}