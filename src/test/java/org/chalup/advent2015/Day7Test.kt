package org.chalup.advent2015

import com.google.common.truth.Truth.assertThat
import org.chalup.advent2015.Day7.CircuitElement.Input.Signal
import org.chalup.advent2015.Day7.CircuitElement.Input.Wire
import org.junit.jupiter.api.Test

@OptIn(ExperimentalUnsignedTypes::class)
class Day7Test {
    @Test
    fun `should simulate test circuit`() {
        assertThat(Day7.simulateInstructions(instructions)).containsExactlyEntriesIn(
            expectedSimulation
                .map { (wireId, signal) -> Wire(wireId) to Signal(signal.toUShort()) }
                .toMap()
        )
    }

    @Suppress("unused")
    companion object {
        val instructions = listOf("123 -> x",
                                  "456 -> y",
                                  "x AND y -> d",
                                  "x OR y -> e",
                                  "x LSHIFT 2 -> f",
                                  "y RSHIFT 2 -> g",
                                  "NOT x -> h",
                                  "NOT y -> i")

        val expectedSimulation = mapOf("d" to 72,
                                       "e" to 507,
                                       "f" to 492,
                                       "g" to 114,
                                       "h" to 65412,
                                       "i" to 65079,
                                       "x" to 123,
                                       "y" to 456)
    }
}