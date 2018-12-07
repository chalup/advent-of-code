package org.chalup.advent2015

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test


class Day6Test {
    @Test
    fun `should count the number of lights on a grid`() {
        assertThat(Day6.countTurnedOnLightsAfterApplyingAllCommands(commands)).isEqualTo(1_000_000 - 1_000 - 4)
    }

    @Test
    fun `should calculate total lights brightness of the grid`() {
        assertThat(Day6.determineGridBrightnessAfterApplyingAllCommands(commands)).isEqualTo(1_000_000 + 2_000 - 4)
    }

    @Suppress("unused")
    companion object {
        val commands = listOf("turn on 0,0 through 999,999",
                              "toggle 0,0 through 999,0",
                              "turn off 499,499 through 500,500")
    }
}