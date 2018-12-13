package org.chalup.advent2018

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class Day12Test {
    @Test
    fun `should calculate high score`() {
        assertThat(
            Day12.calculateChecksum(
                generationNumber = 20,
                generations = Day12.parseInput(testInput).let { (rules, initialState) -> Day12.gameOfPots(rules, initialState) }
            )
        ).isEqualTo(325)
    }

    companion object {
        val testInput = """
        initial state: #..#.#..##......###...###

        ...## => #
        ..#.. => #
        .#... => #
        .#.#. => #
        .#.## => #
        .##.. => #
        .#### => #
        #.#.# => #
        #.### => #
        ##.#. => #
        ##.## => #
        ###.. => #
        ###.# => #
        ####. => #
    """.trimIndent().lines()
    }
}