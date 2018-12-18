package org.chalup.advent2018

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class Day18Test {
    @Test
    fun `should simulate the world correctly`() {
        val input = """
                         .#.#...|#.
                         .....#|##|
                         .|..|...#.
                         ..|#.....#
                         #.#|||#|#|
                         ...#.||...
                         .|....|...
                         ||...#|.#|
                         |.||||..|.
                         ...#.|..|.
                         """.trimIndent().lines()

        assertThat(Day18.part1(input)).isEqualTo(1147)
    }
}